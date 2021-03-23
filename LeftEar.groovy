//Your code hereimport com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import java.nio.file.Paths

import com.neuronrobotics.bowlerstudio.scripting.ScriptingEngine
import com.neuronrobotics.bowlerstudio.vitamins.Vitamins

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.svg.SVGLoad
import eu.mihosoft.vrl.v3d.*
def url="https://github.com/Halloween2020TheChild/Baby-Yoda.git"
def branch="master"

File earFile = ScriptingEngine.fileFromGit(url,
		"Left Ear-DownRes.stl");
//Vitamins.clear();
CSG ear  = Vitamins.get(earFile)
//		.roty(90)
//		.toZMin()
//ear=ear.movex(-ear.centerX)
//			.movey(-ear.centerY)

//FileUtil.write(Paths.get(earFile.getAbsolutePath()),
//		ear.toStlString());
		
File earCoreFile = ScriptingEngine.fileFromGit(url,
		"LeftEar-DownRes-4mmInset.stl");
CSG earCore; 
if(!earCoreFile.exists()) {
	println "Making ear core"
	earCore=ear.toolOffset(-4)
	ScriptingEngine.pushCodeToGit(url, branch, "LeftEar-DownRes-4mmInset.stl", earCore.toStlString(), "Making ear core ", true)
}else
	earCore=Vitamins.get(earCoreFile)

return [ear,earCore]