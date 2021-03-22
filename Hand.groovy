import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.bowlerstudio.vitamins.Vitamins

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.svg.SVGLoad
import eu.mihosoft.vrl.v3d.*

double moldHeight = 100
double moldLowering = -15
File Right_Hand = ScriptingEngine.fileFromGit("https://github.com/vermontolympian/Baby-Yoda.git",
"DownRes-Right-Hand.stl");		

File core_file = ScriptingEngine.fileFromGit("https://github.com/vermontolympian/Baby-Yoda.git",
	"DownRes-RightInsert-4mm.stl");
CSG core = Vitamins.get(core_file)
				.toZMin()
core=core
				.movex(-core.centerX-0.75)
				.movey(-core.centerY)
	
CSG Right  = Vitamins.get(Right_Hand)						//Get hand STL
			.scale(2)									//Scale to "life-like" size
			.toZMin()
Right=Right
			.movex(-Right.centerX)
			.movey(-Right.centerY)
			
CSG moldCore = new Cube(70,60,moldHeight).toCSG()						//Create front mold piece
			.toZMin()
			.movez(moldLowering)

CSG Cylinder = (CSG)(ScriptingEngine.gitScriptRun(
            "https://github.com/Halloween2020TheChild/Baby-Yoda.git", // git location of the library
            "handPlug.groovy" ,null))
			.toZMax()
			.movez(0.5)
File f = ScriptingEngine
.fileFromGit(
	"https://github.com/Halloween2020TheChild/Baby-Yoda.git",//git repo URL
	"master",//branch
	"draftLine.SVG"// File from within the Git repo
	)
println "Extruding SVG "+f.getAbsolutePath()
SVGLoad s = new SVGLoad(f.toURI())
CSG draftLine = s.extrudeLayerToCSG(moldHeight,"Slice 1")
				.toYMin()
				.movey(moldCore.getMinY()-0.5)
				.movex(moldCore.getMinX())
				.movez(moldLowering)
				
CSG vitamin_capScrew_M5x100 = Vitamins.get("capScrew", "M5x100")
	.rotx(90)
	.movey(moldCore.getMaxY())
	
CSG lower = vitamin_capScrew_M5x100.movez(moldLowering/2)
CSG upper = vitamin_capScrew_M5x100.movez(moldHeight+moldLowering*1.5)
CSG upperL = upper.movex(-20)
CSG upperR = upper.movex(20)

CSG Pry = new Cube (15,4,20).toCSG()						//Create pry location 2

CSG pry1 = Pry.toXMax().movex(moldCore.getMaxX()+1)
					.movey(1.5)
CSG pry2 = Pry.toXMin().movex(moldCore.getMinX()-1)
					.movey(4.5)
def moldA = moldCore.difference(draftLine,Cylinder,Right ,lower,upperL,upperR,pry1,pry2)
def moldB = draftLine.difference(Cylinder,Right ,lower,upperL,upperR,pry1,pry2)
def moldCoreFinal = core.union(Cylinder.difference(lower))

return [moldB,moldCoreFinal,moldA]

