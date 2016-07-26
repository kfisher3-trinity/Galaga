package game2

import cs2.util.Vec2
import scalafx.scene.image.Image
import scala.collection.mutable
import scala.collection.immutable
import scalafx.scene.canvas.GraphicsContext


/**
 * @author eherbert
 */
class Boss(val bossImg:Image,
           val initVel:Vec2,
           val initPos:Vec2,
           val bossBulletImg:Image,
           var bossCurrentLives:Int,
           val bossAnimations:mutable.Buffer[Image],
           val shootTimer:Double,
           val bossAnimation:Image) extends Player(bossImg,
                                         initVel,
                                         initPos,
                                         bossBulletImg,
                                         0.0,
                                         bossCurrentLives,
                                         bossAnimations,
                                         bossAnimation) {
  
  var queue = mutable.Queue[Vec2]()
  var internalShootTimer = shootTimer
  
  def move(delta:Double) {
    var temp = queue.front
    queue = queue.drop(1)
    if(temp.x < 0 && spritePos.x < 1) temp.x = 0
    if(temp.x > 0 && spritePos.x + bossImg.width.apply.toInt > 1499) temp.x = 0
    if(temp.y < 0 && spritePos.y < 1) temp.y = 0
    if(temp.y > 0 && spritePos.y + bossImg.height.apply.toInt > 799) temp.y = 0
    move(temp,delta)
  }
  
  def displayCheck(gc:GraphicsContext) {
    if(bossCurrentLives > 0) display(gc)
    if(bossCurrentLives == 1) gc.drawImage(bossAnimations(4),spritePos.x,spritePos.y)
    else if(bossCurrentLives == 2) gc.drawImage(bossAnimations(3),spritePos.x,spritePos.y)
    else if(bossCurrentLives == 3) gc.drawImage(bossAnimations(2),spritePos.x,spritePos.y)
    else if(bossCurrentLives == 4) gc.drawImage(bossAnimations(1),spritePos.x,spritePos.y)
    else if(bossCurrentLives == 5) gc.drawImage(bossAnimations(0),spritePos.x,spritePos.y)
    //else if(bossCurrentLives == 0) gc.drawImage(bossAnimations(5),spritePos.x-bossAnimations(5).width.apply.toInt/4,spritePos.y-bossAnimations(5).height.apply.toInt/4)
  }
  
  def display2(gc:GraphicsContext) {
    displayCheck(gc:GraphicsContext)
    bullets.foreach(bullet => bullet.display2(gc))
    pendingBullets.foreach(bullet => bullet._1.display(gc,bossAnimation))
  }
  
  def bossShoot(playerX:Double,playerY:Double) {
    if(internalShootTimer < 0) {
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
      internalShootTimer = shootTimer
    } else internalShootTimer -= math.random
  }
  
  override def copy:Boss = {
    val temp = new Boss(bossImg,
             new Vec2(initVel.x,initVel.y),
             new Vec2(spritePos.x,spritePos.y),
             bossBulletImg,
             bossCurrentLives + 0,
             bossAnimations,
             shootTimer + 0,
             animation)
    queue.foreach(unit => temp.queue.enqueue(new Vec2(unit.x,unit.y)))
    temp.internalShootTimer = internalShootTimer
    bullets.foreach(bullet => temp.bullets.prepend(bullet.copy))
    deadBullets.foreach(bullet => temp.deadBullets.prepend(bullet.copy))
    temp
  }
  
  def timeStep(gc:GraphicsContext,delta:Double,playerX:Double,playerY:Double,newPlayerVec2:Vec2) {
    try {
      queue.enqueue(newPlayerVec2)
      if(queue.length > 50) move(delta)
      bossShoot(playerX,playerY)
      bullets.foreach(bullet => { bullet.timeStep(gc, delta) })
      deadBullets.foreach(bullet => { bullet.timeStep(gc, delta) })
    } catch { case e:NullPointerException => println("Boss.timeStep -> NullPointerException")}
  }
  
}