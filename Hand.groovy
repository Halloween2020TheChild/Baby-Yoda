import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.bowlerstudio.vitamins.Vitamins

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.svg.SVGLoad
import eu.mihosoft.vrl.v3d.*

double moldHeight = 100
double moldLowering = -15
double coneheight=40
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
			.movez(-1)
			
CSG moldCore = new Cube(70,60,moldHeight+coneheight).toCSG()						//Create front mold piece
			.toZMin()
			.movez(moldLowering)
CSG rc= new RoundedCube(	40,// X dimention
				39,// Y dimention
				39//  Z dimention
				)
				.cornerRadius(4)// sets the radius of the corner
				.toCSG()// converts it to a CSG tor display
				.toZMax()
				.movez(0.5)
				.movex(-5)

CSG Cylinder =new RoundedCube(	40,// X dimention
				40,// Y dimention
				40//  Z dimention
				)
				.cornerRadius(4)// sets the radius of the corner
				.toCSG()// converts it to a CSG tor display
				.toZMax()
				.movez(1)
				.movex(-5)
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
CSG draftLine = s.extrudeLayerToCSG(moldHeight+coneheight,"Slice 1")
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
					.movey(1.5)// hand adjust to meet the part line
CSG pry2 = Pry.toXMin().movex(moldCore.getMinX()-8)
					.movey(4.5)// hand adjust to meet the part line
def pourLocation = new Transform()
		.movez(moldHeight+moldLowering)
		.movey(5)
		.movex(2)
CSG pourHole = new Cylinder(2, // Radius at the bottom
                      		2, // Radius at the top
                      		40, // Height
                      		(int)8 //resolution
                      		).toCSG()//convert to CSG to display    
				.movez(-12)
				.transformed(pourLocation)
				
CSG pourCone =new Cylinder(2, // Radius at the bottom
                      		40, // Radius at the top
                      		40, // Height
                      		(int)10 //resolution
                      		).toCSG()//convert to CSG to display  
							.transformed(pourLocation)
							.intersect(moldCore.movez(40))
def toVent(def list) {
	def paths=[]
	for(int i=0;i<list.size()-1;i++) {
		paths.add(list[i].union(list[i+1]).hull())
	}
	return CSG.unionAll(paths)
}
def vents = [new Cube(4).toCSG()
			.toZMin()
			.movex(9)
			.movey(4),
 new Cube(4).toCSG()
			.toZMin()
			.movex(30)
			.movez(30),
new Cube(4).toCSG()
			.toZMin()
			.movex(30)
			.movez(100),
			new Cube(10).toCSG()
			.toZMin()
			.movex(100)
			.movez(100)
			]
def vents2 = [new Cube(4).toCSG()
				.toZMin()
				.movex(-18)
				.movey(0),
	 new Cube(4).toCSG()
				.toZMin()
				.movey(4)
				.movex(-30)
				.movez(20),
	new Cube(4).toCSG()
				.toZMin()
				.movey(4)
				.movex(-30)
				.movez(100),
				new Cube(10).toCSG()
				.toZMin()
				.movey(20)
				.movex(-100)
				.movez(100)
				]
CSG vent = toVent(vents)
			.union(new Cube(12,4,4).toCSG()
				.toZMin()
				.toXMin()
				.movez(52)
				.movey(0)
				.movex(18))

CSG vent2 = toVent(vents2)
.union(new Cube(16,4,4).toCSG()
	.toZMin()
	.toXMax()
	.movez(69)
	.movey(2)
	.movex(-15))


									
def moldA = moldCore.difference(draftLine,Cylinder,Right ,lower,upperL,upperR,pry1,pry2,pourHole,pourCone,vent,vent2).rotx(90).movey(45)
def moldB = draftLine.difference(Cylinder,Right ,lower,upperL,upperR,pry1,pry2,pourHole,pourCone,vent,vent2).rotx(-90).movey(-45)
def moldCoreFinal = core.union(rc.difference(lower))
def moldALeft = moldA.mirrory().movex(100)
def moldBLeft  = moldB.mirrory().movex(100)
def moldCoreFinalLeft  = moldCoreFinal.mirrory().movex(100)

moldA.setName("RightMoldA")
moldB.setName("RightMoldB")
moldCoreFinal.setName("RightMoldCore")
moldALeft.setName("LeftMoldA")
moldBLeft.setName("LeftMoldB")
moldCoreFinalLeft.setName("LeftMoldCore")

return [moldB,moldCoreFinal,moldA,moldALeft,moldBLeft,moldCoreFinalLeft]

