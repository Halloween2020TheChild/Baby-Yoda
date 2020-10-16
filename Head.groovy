File Head_stl = ScriptingEngine.fileFromGit("https://github.com/vermontolympian/Baby-Yoda.git",
"Head.stl");	
CSG Head = Vitamins.get(Head_stl)
			.rotx(9)
			.toXMin()
			.toYMin()
			.toZMin()

CSG Cube1 = new Cube(200,100,160).toCSG()
			.toXMin()
			.toYMin()
			.toZMin()
			.movex(-20)
			.movey(100)

CSG Cube2 = new Cube(200,100,160).toCSG()
			.toXMin()
			.toYMin()
			.toZMin()
			.movex(-20)

CSG Back = Cube1.difference(Head)
			.rotx(-90)
			.toZMin()
			.movey(20)

//CSG Front = Cube2.difference(Head)
//			.rotx(90)
//			.toZMin()
//			.movey(-20)

return[Back];