package game2

import cs2.util.Vec2
import scalafx.scene.image.Image
import scalafx.scene.canvas.GraphicsContext
import scala.collection.mutable


/**
 * @author eherbert
 */

class Enemy(val img:Image,
            val vel:Vec2,
            val pos:Vec2,
            val bulletImg:Image,
            val pointValue:Double,
            val animation:Image) extends Sprite(img,
                                         vel,
                                         pos){
  
  var bullets = mutable.Buffer[Bullet]()
  var pendingBullets = mutable.Map[Bullet,Double]()
  var deadBullets = mutable.Buffer[Bullet]()
  
  def display2(gc:GraphicsContext) {
    display(gc)
    bullets.foreach(bullet => bullet.display2(gc))
    pendingBullets.foreach(bullet => bullet._1.display(gc,animation))
  }
  
  def shoot(playerX:Double,playerY:Double) {
    val opp2 = playerX-(spritePos.x+img.width.apply/2)
    val adj2 = playerY-(spritePos.y+img.height.apply/2)
    val opp = math.abs(opp2)
    val adj = math.abs(adj2)
    val theta = math.atan(opp/adj)
    val x2 = if(opp >= adj) opp/opp else opp/adj
    val y2 = if(opp >= adj) adj/opp else adj/adj
    
    bullets.prepend(new Bullet(bulletImg,
                               if(opp2 < 0 && adj2 < 0) new Vec2(-x2,-y2) else if(opp2 >= 0 && adj2 < 0) new Vec2(x2,-y2) else if(opp2 >= 0 && adj >= 0) new Vec2(x2,y2) else new Vec2(-x2,y2),
                               new Vec2(spritePos.x + img.width.apply/2, spritePos.y + img.height.apply/2)))
  }
  
  def copy:Enemy = {
    val temp = new Enemy(img,
                         new Vec2(vel.x,vel.y),
                         new Vec2(spritePos.x,spritePos.y),
                         bulletImg,
                         pointValue,
                         animation)
    bullets.foreach(bullet => temp.bullets.prepend(bullet.copy))
    pendingBullets.foreach(bullet => temp.pendingBullets += (bullet._1.copy -> bullet._2.toInt))
    deadBullets.foreach(bullet => temp.deadBullets.prepend(bullet.copy))
    temp
  }
  
  def timeStep(gc:GraphicsContext,delta:Double,currentVel:Vec2) {
    move(currentVel)
    bullets.foreach(bullet => { bullet.timeStep(gc, delta) })
  }
  
}