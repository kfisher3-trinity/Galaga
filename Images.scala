package game2

import scalafx.scene.media.Media
import scalafx.scene.image.Image
import java.io.File
import scala.collection.mutable
import scalafx.scene.media.MediaPlayer

/**
 * @author eherbert
 */
object Images {
  val board = new Image("file:board.png", 50, 50, true, true)
  val paper = new Image("file:paper.png", 50, 50, true, true)
  val lens = new Image("file:camera.png", 50, 50, true, true)
  val star = new Image("file:star.png", 50, 50, true, true)
  val dietCoke = new Image("file:dietCoke.png", 100, 100, true, true)
  val dietCokeN = new Image("file:dietCokeN.png", 300, 100, true, true)
  val dietCokeNE = new Image("file:dietCokeNE.png", 300, 100, true, true)
  val dietCokeE = new Image("file:dietCokeE.png", 300, 100, true, true)
  val dietCokeSE = new Image("file:dietCokeSE.png", 300, 100, true, true)
  val dietCokeS = new Image("file:dietCokeS.png", 300, 100, true, true)
  val dietCokeSW = new Image("file:dietCokeSW.png", 300, 100, true, true)
  val dietCokeW = new Image("file:dietCokeW.png", 300, 100, true, true)
  val dietCokeNW = new Image("file:dietCokeNW.png", 300, 100, true, true)
  val dietCokeTop = new Image("file:cokeTop.png", 25, 25, true, true)
  val shield = new Image("file:shield.png", 135, 135, true, true)
  val playerAnimationBuffer = mutable.Buffer[Image](dietCokeN, dietCokeNE, dietCokeE, dietCokeSE, dietCokeS, dietCokeSW, dietCokeW, dietCokeNW)
  val pepsi = new Image("file:pepsi.png", 50, 50, true, true)
  val pepsiTop = new Image("file:pepsiTop.png", 25, 25, true, true)
  val dietCokeLogo = new Image("file:dietCokeLogo2.png")
  val pepsiLogo = new Image("file:pepsiLogo2.png")
  val hollywood = new Image("file:hollywood.png", 1500, 800, true, true)
  val rc = new Image("file:rc.png", 300, 300, true, true)
  val rc1 = new Image("file:rc1.png", 300, 300, true, true)
  val rc2 = new Image("file:rc2.png", 300, 300, true, true)
  val rc3 = new Image("file:rc3.png", 300, 300, true, true)
  val rc4 = new Image("file:rc4.png", 300, 300, true, true)
  val rc5 = new Image("file:rc5.png", 300, 300, true, true)
  val fire = new Image("file:fire.gif", 300, 300, true, true)
  val bossAnimationBuffer = mutable.Buffer[Image](rc1, rc2, rc3, rc4, rc5, fire)
  
  val sound = new Media(new File("songOne.wav").toURI().toString())
  var songOneMediaPlayer = new MediaPlayer(sound)
  songOneMediaPlayer.onRepeat
  
  val sound2 = new Media(new File("canOpen.wav").toURI().toString())
  var songTwoMediaPlayer = new MediaPlayer(sound2)
  
  val sound3 = new Media(new File("pouring.wav").toURI().toString())
  var songThreeMediaPlayer = new MediaPlayer(sound3)
}