import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.bowlerstudio.vitamins.Vitamins

import eu.mihosoft.vrl.v3d.CSG
File Left_Hand = ScriptingEngine.fileFromGit("https://github.com/vermontolympian/Baby-Yoda.git",
	"DownRes-Left-Hand.stl");								
CSG Left  = Vitamins.get(Left_Hand)						//Get hand STL
			.scale(2)									//Scale to "life-like" size
			.toXMin()
			.toYMin()
			.toZMin()

CSG clearenceFit = Left.toolOffset(-4)

return[clearenceFit];