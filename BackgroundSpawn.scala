package game2

import scalafx.scene.image.Image
import scalafx.scene.canvas.GraphicsContext
import scala.collection.mutable
import cs2.util.Vec2


/**
 * @author eherbert
 */
class BackgroundSpawn(val img:Image,
                      val spawnTimer:Double,
                      val y:Double) {
  
  var internalSpawnTimer = spawnTimer
  var parts = mutable.Buffer[BackgroundCloud]()
 
  def spawn {
    if(internalSpawnTimer < 0) {
      val ratio = util.Random.nextInt(50).toDouble/100.0
      parts.prepend(new BackgroundCloud(img, new Vec2(math.random/2,0), new Vec2(-300,y),ratio))
      internalSpawnTimer = spawnTimer
    } else internalSpawnTimer -= math.random
  }
  
  def display2(gc:GraphicsContext) { parts.foreach(part => part.display2(gc)) }
  
  def copy:BackgroundSpawn = {
    val temp = new BackgroundSpawn(img,spawnTimer + 0,y + 0)
    temp.internalSpawnTimer = internalSpawnTimer + 0
    parts.foreach(unit => temp.parts.prepend(unit.copy))
    temp
  }
  
  def timeStep(gc:GraphicsContext) {
    spawn
    parts.foreach(part => part.timeStep(gc))
  }
}