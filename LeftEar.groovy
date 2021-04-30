//Your code here
import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

import com.neuronrobotics.bowlerstudio.BowlerStudioController
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

void ignore(String url, String filename) {
	boolean add=false;
	File earCoreFile = ScriptingEngine.fileFromGit(url,
		".gitignore");
	if(earCoreFile.exists()) {
		String data = earCoreFile.text
		if(!data.contains(filename)) {
			ScriptingEngine.pushCodeToGit(url, ScriptingEngine.getBranch(url), ".gitignore", data+"\n"+filename, "updating gitignore ", true)
		}
	}else {
		ScriptingEngine.pushCodeToGit(url, ScriptingEngine.getBranch(url), ".gitignore", filename, "creating gitignore ", true)
	}
	
}

CSG makeCachedFile(String url, String filename, Closure makeit) {
	if(!filename.toLowerCase().endsWith(".stl"))
		filename=filename+".stl"
	File earCoreFile = ScriptingEngine.fileFromGit(url,
		filename);
	CSG earCore;
	if(!earCoreFile.exists()) {
		println "Making Cached "+filename
		earCore=makeit()
		def earCoreToStlString = earCore.toStlString()
		if(earCoreToStlString.length()<10000000-1)
			ScriptingEngine.pushCodeToGit(url, ScriptingEngine.getBranch(url), filename, earCoreToStlString, "Making Cached "+filename, true)
		else {
			println "STL too big for git, stored on disk"
			ignore( url,  filename)
		}
	}else
		println "Loading cached "+filename
		earCore=Vitamins.get(earCoreFile)
	return earCore
}

List<Polygon> makeCachedSVG(String url, String filename, Closure makeit) {
	if(!filename.toLowerCase().endsWith(".svg"))
		filename=filename+".svg"
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
		def svgMake = svg.make()
		if(svgMake.length()<10000000-1)
			ScriptingEngine.pushCodeToGit(url, ScriptingEngine.getBranch(url), filename, svgMake, "Making Cached SVG "+filename, true)
		else {
			println "SVG too big for git, stored on disk"
			ignore( url,  filename)
		}
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
Vitamins.clear();
CSG ear  = Vitamins.get(earFile)

//		.roty(90)
//		.toZMin()
//ear=ear.movex(-ear.centerX)
//			.movey(-ear.centerY)		
File earCoreFile = ScriptingEngine.fileFromGit(url,
		"LeftEar-DownRes-4mmInset.stl");
 
//if(!earCoreFile.exists()) {
	println "Making ear core"
	earCore=ear.toolOffset(-4)
	ScriptingEngine.pushCodeToGit(url, branch, "LeftEar-DownRes-4mmInset.stl", earCore.toStlString(), "Making ear core ", true)
//}else
//	earCore=Vitamins.get(earCoreFile)


CSG.setProgressMoniter(new ICSGProgress() {
		@Override
		public void progressUpdate(int currentIndex, int finalIndex, String type, CSG intermediateShape) {
			System.out.println(type+"  "+currentIndex+" of "+finalIndex);
		}
	})

def earCore=makeCachedFile(url,"LeftEar-DownRes-4mmInset-2.stl",{
	return ear.toolOffset(-4)
})
println "Number of polys in first gen: "+ earCore.getPolygons().size()

//earCore=makeCachedFile(url,"LeftEar-DownRes-4mmInset-rev2.stl",{
//	return ear.toolOffset(-4)
//})
//
//println "Number of polys in second gen: "+ earCore.getPolygons().size()


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


def polys = makeCachedSVG(url, "earCoreSlice.svg",{  
	println "Slicing ear"
	return Slice.slice(earCore,slicePlane,0)
}) 
println "Extruding neck"
//def earNub = makeCachedFile(url, "earNub.stl",{Extrude.polygons(polys.get(1).transformed(new Transform()
//	.movez(neckLength/2)	), polys.get(1).transformed(slicePlane))})
//def Neck = makeCachedFile(url, "neck.stl",{Extrude.polygons(polys.get(0), polys.get(0).transformed(slicePlane))})
def earNub = Extrude.polygons(polys.get(1).transformed(new Transform()
	.movez(neckLength/2)	), polys.get(1).transformed(slicePlane)).movez(0.5)
def Neck = Extrude.polygons(polys.get(0), polys.get(0).transformed(slicePlane))
Plane.setDebugger(new IPolygonDebugger() {
	void display( List<Polygon> poly) {
	BowlerStudioController.getBowlerStudio().addObject(poly, null);
	}
})

def newNeck =Neck.difference(earNub)
//CoreWithExtention=makeCachedFile(url,"LeftEarCoreWithExtention.stl",{
//	return newNeck.union(earCore)
//})

//def extendedCore = earCore.union(Neck)
CSG post=makeCachedFile(url,"EarPostNeckPart.stl",{
	CSG corePlug = earCore.intersect(boxOfPlug)
	return corePlug.union(corePlug.movez(-neckLength)).hull().intersect(boxOfPlug)
})
return [
	earCore,
	newNeck
	]


