package game2

import cs2.util.Vec2
import scalafx.scene.image.Image
import scalafx.scene.canvas.GraphicsContext
import scala.collection.mutable
import scala.collection.mutable

/**
 * @author eherbert
 */

class Player(val img:Image,
            var vel:Vec2,
            val pos:Vec2,
            val bulletImg:Image,
            var currentScore:Double,
            var currentLives:Double,
            var animations:mutable.Buffer[Image],
            val animation:Image) extends Sprite(img,
                                         vel,
                                         pos) {
  
  var bullets = mutable.Buffer[Bullet]()
  var pendingBullets = mutable.Map[Bullet,Double]()
  var deadBullets = mutable.Buffer[Bullet]()
  var lazerBuffer = mutable.Buffer[Lazer]()
  var lastVel = new Vec2(0,0)
  
  def moveLeft(delta:Double) { super.move(new Vec2(-1,0),delta) }
  def moveRight(delta:Double) {  super.move(new Vec2(1,0),delta) }
  def moveUp(delta:Double) {  super.move(new Vec2(0,-1),delta) }
  def moveDown(delta:Double) {  super.move(new Vec2(0,1),delta) }
  
  def display(gc:GraphicsContext,leftPressed:Boolean,rightPressed:Boolean,upPressed:Boolean,downPressed:Boolean) {
    val x = spritePos.x-87
    val y = spritePos.y-100
    
    if(leftPressed && rightPressed && upPressed && !downPressed) { gc.drawImage(animations(4), x,y) }
    else if(leftPressed && rightPressed && !upPressed && downPressed) { gc.drawImage(animations(0), x,y) }
    else if(leftPressed && !rightPressed && upPressed && downPressed) { gc.drawImage(animations(2), x,y) }
    else if(!leftPressed && rightPressed && upPressed && downPressed) { gc.drawImage(animations(6), x,y) }
    else if(leftPressed && !rightPressed && upPressed && !downPressed) { gc.drawImage(animations(3), x,y) }
    else if(!leftPressed && rightPressed && upPressed && !downPressed) { gc.drawImage(animations(5), x,y) }
    else if(leftPressed && !rightPressed && !upPressed && downPressed) { gc.drawImage(animations(1), x,y) }
    else if(!leftPressed && rightPressed && !upPressed && downPressed) { gc.drawImage(animations(7), x,y) }
    else if(leftPressed && !rightPressed && !upPressed && !downPressed) { gc.drawImage(animations(2), x,y) }
    else if(!leftPressed && rightPressed && !upPressed && !downPressed) { gc.drawImage(animations(6), x,y) }
    else if(!leftPressed && !rightPressed && upPressed && !downPressed) { gc.drawImage(animations(4), x,y) }
    else if(!leftPressed && !rightPressed && !upPressed && downPressed) { gc.drawImage(animations(0), x,y) }
    else {}
    
    super.display(gc)
  }
  
  def display2(gc:GraphicsContext,leftPressed:Boolean,rightPressed:Boolean,upPressed:Boolean,downPressed:Boolean) {
    display(gc,leftPressed,rightPressed,upPressed,downPressed)
    bullets.foreach(bullet => bullet.display2(gc))
    pendingBullets.foreach(bullet => bullet._1.display(gc,animation))
    lazerBuffer.foreach(lazer => lazer.display2(gc))
  }
  
  def shoot(x:Double,y:Double,leftPressed:Boolean,rightPressed:Boolean,upPressed:Boolean,downPressed:Boolean) {
    val opp2 = x-(spritePos.x+img.width.apply/2)
    val adj2 = y-(spritePos.y+img.height.apply/2)
    val opp = math.abs(opp2)
    val adj = math.abs(adj2)
    val theta = math.atan(opp/adj)
    val x2 = if(opp >= adj) opp/opp else opp/adj
    val y2 = if(opp >= adj) adj/opp else adj/adj
    val tempVel = if(opp2 < 0 && adj2 < 0) new Vec2(-x2,-y2) else if(opp2 >= 0 && adj2 < 0) new Vec2(x2,-y2) else if(opp2 >= 0 && adj >= 0) new Vec2(x2,y2) else new Vec2(-x2,y2)
    val tempPos = new Vec2(spritePos.x+img.width.apply/2-bulletImg.width.apply/2,spritePos.y+img.height.apply/2-bulletImg.height.apply/2)
    
    lastVel = tempVel
    
    /*if(leftPressed) tempVel.x -= 1
    if(rightPressed) tempVel.x += 1
    if(upPressed) tempVel.y -= 1
    if(downPressed) tempVel.y += 1*/
    
    bullets.prepend(new Bullet(bulletImg,
                    tempVel,
                    tempPos))       
  }
  
  def shootLazer(x:Double,y:Double,leftPressed:Boolean,rightPressed:Boolean,upPressed:Boolean,downPressed:Boolean) {
    val opp2 = x-(spritePos.x+img.width.apply/2)
    val adj2 = y-(spritePos.y+img.height.apply/2)
    val opp = math.abs(opp2)
    val adj = math.abs(adj2)
    val theta = math.atan(opp/adj)
    val x2 = if(opp >= adj) opp/opp else opp/adj
    val y2 = if(opp >= adj) adj/opp else adj/adj
    val tempVel = if(opp2 < 0 && adj2 < 0) new Vec2(-x2,-y2) else if(opp2 >= 0 && adj2 < 0) new Vec2(x2,-y2) else if(opp2 >= 0 && adj >= 0) new Vec2(x2,y2) else new Vec2(-x2,y2)
    val tempPos = new Vec2(spritePos.x+img.width.apply/2-bulletImg.width.apply/2,spritePos.y+img.height.apply/2-bulletImg.height.apply/2)
    
    lastVel = tempVel
    
    /*if(leftPressed) tempVel.x -= 1
    if(rightPressed) tempVel.x += 1
    if(upPressed) tempVel.y -= 1
    if(downPressed) tempVel.y += 1*/
    
    println("created Lazer")
    
    lazerBuffer.prepend(new Lazer(bulletImg,
                    tempVel,
                    tempPos))       
  }
  
  def copy:Player = {
    val temp = new Player(img, new Vec2(spriteVel.x,spriteVel.y), new Vec2(spritePos.x,spritePos.y), bulletImg, currentScore.toDouble, currentLives.toDouble, animations, animation)
    bullets.foreach(bullet => temp.bullets.prepend(bullet.copy))
    pendingBullets.foreach(bullet => temp.pendingBullets += (bullet._1.copy -> bullet._2.toInt))
    deadBullets.foreach(bullet => temp.deadBullets.prepend(bullet.copy))
    lazerBuffer.foreach(lazer => temp.lazerBuffer.prepend(lazer.copy))
    temp.lastVel = new Vec2(lastVel.x,lastVel.y)
    temp
  }
  
  def timeStep(gc:GraphicsContext,leftPressed:Boolean,rightPressed:Boolean,upPressed:Boolean,downPressed:Boolean,delta:Double) {
    try {
      if(leftPressed) if(spritePos.x > 0) moveLeft(delta)
      if(rightPressed) if(spritePos.x + img.width.apply < 1500) moveRight(delta)
      if(downPressed) if(spritePos.y + img.height.apply < 800) moveDown(delta)
      if(upPressed) if(spritePos.y > 0) moveUp(delta)
      bullets.foreach(bullet => { bullet.timeStep(gc, delta) })
      lazerBuffer.foreach(lazer => lazer.timeStep(gc,delta))
    } catch { case e:NullPointerException => {println("Player.timeStep -> NullPointerException")}}
  }
}