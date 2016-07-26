package game2

import cs2.util.Vec2
import scalafx.scene.image.Image
import scalafx.scene.canvas.GraphicsContext


/**
 * @author eherbert
 */

class Bullet(val img:Image,
             val vel:Vec2,
             val pos:Vec2) extends Sprite(img,
                                          vel,
                                          pos) {
  
  def copy:Bullet = { new Bullet(img,new Vec2(vel.x,vel.y),new Vec2(spritePos.x,spritePos.y)) }
  
  def display2(gc:GraphicsContext) { display(gc) }
  
  def timeStep(gc:GraphicsContext,delta:Double) {
    move(vel*delta*200)
  }
  
}