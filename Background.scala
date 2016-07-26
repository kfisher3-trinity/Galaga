package game2

import scala.collection.mutable
import scalafx.scene.image.Image
import scalafx.scene.canvas.GraphicsContext


/**
 * @author eherbert
 */
class Background(val img:Image,
                 val items:List[Image]) {
  
  var spawns = mutable.Buffer[BackgroundSpawn]()
  
  for(i <- items.indices) {
    spawns.prepend(new BackgroundSpawn(items(i),util.Random.nextInt(400)+500,util.Random.nextInt(600)-100))
    spawns.prepend(new BackgroundSpawn(items(i),util.Random.nextInt(400)+500,util.Random.nextInt(600)-100))
    spawns.prepend(new BackgroundSpawn(items(i),util.Random.nextInt(400)+500,util.Random.nextInt(600)-100))
  }
  
  def copy:Background = {
    val temp = new Background(img,items)
    spawns.foreach(spawn => temp.spawns.prepend(spawn.copy))
    temp
  }
  
  def display2(gc:GraphicsContext) {
    spawns.foreach(spawn => spawn.display2(gc))
    gc.drawImage(img, 0, -200)
  }
  
  def timeStep(gc:GraphicsContext) {
    spawns.foreach(spawn => spawn.timeStep(gc))
    gc.drawImage(img, 0, -200)
  }
}