package game2

import cs2.util.Vec2
import scalafx.scene.image.Image
import scala.collection.mutable
import scalafx.scene.canvas.GraphicsContext


/**
 * @author eherbert
 */

class EnemySwarm(val nRows:Int,
                 val nCols:Int,
                 val initPos:Vec2,
                 val enemyImg:Image,
                 val enemyVel:Vec2,
                 val bulletImg:Image,
                 val enemyPointValue:Double,
                 val shootTimer:Double,
                 val animation:Image) {
  
  var numberOfEnemies = nRows*nCols
  var currentPos = initPos
  var currentVel = new Vec2(math.random-0.5,math.random-0.5)
  var internalShootTimer = shootTimer
  
  var enemies = mutable.Buffer[Enemy]()
  var pendingEnemies = mutable.Map[Enemy,Double]()
  var deadEnemies = mutable.Buffer[Enemy]()
  var bullets = mutable.Buffer[Bullet]()
  var pendingBullets = mutable.Map[Bullet,Double]()
  var deadBullets = mutable.Buffer[Bullet]()
  
  def shoot(playerX:Double,playerY:Double):Unit = {
    if(internalShootTimer < 0) {
      enemies(if(enemies.length < 2) 0 else util.Random.nextInt(enemies.length-1)).shoot(playerX, playerY)
      internalShootTimer = shootTimer
    } else internalShootTimer -= math.random
  }
  
  def move {
    if(currentPos.x > 1498-(nRows*(enemyImg.width.apply+10)) || currentPos.x < 2 || currentPos.y < 2 || currentPos.y > 798-(nCols*(enemyImg.height.apply+10))) { currentVel = new Vec2(math.random-0.5,math.random-0.5)}
    currentPos += currentVel
  }
  
  def copy:EnemySwarm = {
    val temp = new EnemySwarm(nRows,nCols,new Vec2(initPos.x,initPos.y),enemyImg,new Vec2(enemyVel.x,enemyVel.y),bulletImg,enemyPointValue,shootTimer,animation)
    temp.currentPos = new Vec2(currentPos.x,currentPos.y)
    temp.currentVel = new Vec2(currentVel.x,currentVel.y)
    temp.internalShootTimer = internalShootTimer
    enemies.foreach(enemy => temp.enemies.prepend(enemy.copy))
    pendingEnemies.foreach(bullet => temp.pendingEnemies += (bullet._1.copy -> bullet._2.toInt))
    deadEnemies.foreach(enemy => temp.deadEnemies.prepend(enemy.copy))
    bullets.foreach(bullet => temp.bullets.prepend(bullet.copy))
    pendingBullets.foreach(bullet => temp.pendingBullets += (bullet._1.copy -> bullet._2.toInt))
    deadBullets.foreach(bullet => temp.deadBullets.prepend(bullet.copy))
    temp
  }
  
  def display2(gc:GraphicsContext) {
    enemies.foreach(enemy => enemy.display2(gc))
    bullets.foreach(bullet => bullet.display2(gc))
    pendingBullets.foreach(bullet => bullet._1.display(gc,animation))
    pendingEnemies.foreach(bullet => bullet._1.display(gc,animation))
  }
  
  def timeStep(gc:GraphicsContext,delta:Double,playerX:Double,playerY:Double) {
    try {
      move
      if(enemies.length > 1) shoot(playerX,playerY)
      enemies.foreach(unit => unit.timeStep(gc,delta,currentVel))
      bullets.foreach(bullet => { bullet.timeStep(gc, delta) })
    } catch { case e:NullPointerException => println("EnemySwarm.timeStep -> NullPointerException")}
  }
  
}