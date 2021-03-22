import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cylinder
import eu.mihosoft.vrl.v3d.RoundedCube

CSG rc= new RoundedCube(	40,// X dimention
				40,// Y dimention
				40//  Z dimention
				)
				.cornerRadius(4)// sets the radius of the corner
				.toCSG()// converts it to a CSG tor display
				.toZMax()
				.movez(0.5)
				.movex(-5)
				return rc