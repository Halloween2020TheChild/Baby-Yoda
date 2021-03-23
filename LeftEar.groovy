//Your code hereimport com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import java.nio.file.Paths

import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.bowlerstudio.vitamins.Vitamins

import eu.mihosoft.vrl.v3d.svg.SVGExporter
import eu.mihosoft.vrl.v3d.svg.SVGLoad
import eu.mihosoft.vrl.v3d.*
def url="https://github.com/Halloween2020TheChild/Baby-Yoda.git"
def branch="master"
double moldY = 90
double moldX = 90
double moldZ = 200
double neckLength =10.5

CSG makeCachedFile(String url, String filename, Closure makeit) {
	File earCoreFile = ScriptingEngine.fileFromGit(url,
		filename);
	CSG earCore;
	if(!earCoreFile.exists()) {
		println "Making Cached "+filename
		earCore=makeit()
		ScriptingEngine.pushCodeToGit(url, "master", filename, earCore.toStlString(), "Making Cached "+filename, true)
	}else
		println "Loading cached "+filename
		earCore=Vitamins.get(earCoreFile)
	return earCore
}

List<Polygon> makeCachedSVG(String url, String filename, Closure makeit) {
	File earCoreFile = ScriptingEngine.fileFromGit(url,
		filename);
	List<Polygon> polygons;
	if(!earCoreFile.exists()) {
		println "SVG Making Cached "+filename
		polygons=makeit()
		println "Exporting to SVG"
		SVGExporter svg = new SVGExporter();
		
		for( Polygon p: polygons){
			svg.toPolyLine(p);
			svg.colorTick();
		}
		println "Pushing changes"
		ScriptingEngine.pushCodeToGit(url, "master", filename, svg.make(), "Making Cached SVG "+filename, true)
		
	}
	println "SVG Loading Cached "+filename
	ArrayList<Polygon> list=new ArrayList<Polygon>();
	SVGLoad l=new SVGLoad(earCoreFile.toURI())
	for(String s:l.getLayers()) {
		list.addAll(l.getPolygonByLayers().get(s))
	}
	return list
}

File earFile = ScriptingEngine.fileFromGit(url,
		"Left Ear-DownRes.stl");
//Vitamins.clear();
CSG ear  = Vitamins.get(earFile)

earCore=makeCachedFile(url,"LeftEar-DownRes-4mmInset.stl",{
	return ear.toolOffset(-4)
})

File f = ScriptingEngine
.fileFromGit(
	"https://github.com/Halloween2020TheChild/Baby-Yoda.git",//git repo URL
	"master",//branch
	"draftlineEar.SVG"// File from within the Git repo
	)
println "Extruding SVG "+f.getAbsolutePath()
SVGLoad s = new SVGLoad(f.toURI())
CSG draftLine = s.extrudeLayerToCSG(moldX*2,"X")
				.rotx(-90)
				.rotz(-90)
				.movex(-moldX)
				.rotz(-7)
				
def slicePlane =new Transform()
					.movez(neckLength)	
					.rotY(-5)	
					.rotX(1)	
CSG boxOfPlug=new Cube(moldX,moldY,neckLength).toCSG().toZMax()
					.transformed(slicePlane)


List<Polygon> polys = makeCachedSVG(url, "earCoreSlice.svg",{  
	println "Slicing ear"
	return Slice.slice(earCore,slicePlane,0)
}) .collect{it.transformed(slicePlane)}



CSG post=makeCachedFile(url,"EarPostNeckPart.stl",{
	CSG corePlug = earCore.intersect(boxOfPlug)
	return corePlug.union(corePlug.movez(-neckLength)).hull().intersect(boxOfPlug)
})
return [earCore,boxOfPlug,
	polys
	]


