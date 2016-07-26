package game2

import scala.collection.mutable
import cs2.util.Vec2
import scalafx.scene.image.Image
import cs2.util.Vec2
import scalafx.scene.canvas.GraphicsContext

/**
 * @author eherbert
 */
class Lazer(val img:Image, val initVel:Vec2,val initPos:Vec2) {
  var blocksBuffer = mutable.Buffer[LaserBlock]()
  var pendingBlocksBuffer = mutable.Map[LaserBlock,Double]()
  var deadBlocksBuffer = mutable.Buffer[LaserBlock]()
  var lastPos = initPos
  
  def copy:Lazer = {
    val temp = new Lazer(img, new Vec2(initVel.x.toDouble,initVel.y.toDouble),new Vec2(initPos.x.toDouble,initPos.y.toDouble))
    blocksBuffer.foreach(block => temp.blocksBuffer.prepend(block.copy))
    pendingBlocksBuffer.foreach(block => temp.pendingBlocksBuffer += (block._1.copy -> block._2.toDouble))
    deadBlocksBuffer.foreach(block => temp.deadBlocksBuffer.prepend(block.copy))
    temp.lastPos = new Vec2(lastPos.x.toDouble,lastPos.y.toDouble)
    temp
  }
  
  def display2(gc:GraphicsContext) { blocksBuffer.foreach(block => block.display2(gc)) }
  
  def timeStep(gc:GraphicsContext,delta:Double) {
    val temp = new LaserBlock(img,initVel,new Vec2(lastPos.x + initVel.x, lastPos.y + initVel.y))
    blocksBuffer += temp
    lastPos = new Vec2(temp.initPos.x, temp.initPos.y)
    println(blocksBuffer.length)
    //if(blocksBuffer.length > 100) blocksBuffer = blocksBuffer.dropRight(1)
    blocksBuffer.foreach(block => block.timeStep(gc, delta))
  }
}