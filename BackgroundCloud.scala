package game2

import cs2.util.Vec2
import scalafx.scene.image.Image
import scalafx.scene.canvas.GraphicsContext


/**
 * @author eherbert
 */
class BackgroundCloud(val img:Image,
                      val vel:Vec2,
                      val pos:Vec2,
                      val ratio:Double) extends Sprite(img,
                                                   vel,
                                                   pos) {
  
  //val ratio = util.Random.nextInt(50).toDouble/100.0
  
  def display2(gc:GraphicsContext) { display(gc,img.width.apply*ratio,img.height.apply*ratio) }
  
  def copy:BackgroundCloud = { new BackgroundCloud(img,new Vec2(vel.x,vel.y),new Vec2(spritePos.x,spritePos.y),ratio+0) }
  
  def timeStep(gc:GraphicsContext) {
    move(vel)
    //display(gc,img.width.apply*ratio,img.height.apply*ratio)
  }
  
}