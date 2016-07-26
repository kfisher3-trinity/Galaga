package game2

import scalafx.scene.image.Image
import cs2.util.Vec2
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.paint.Color


/**
 * @author eherbert
 */

abstract class Sprite(val spriteImg:Image,
                      var spriteVel:Vec2,
                      var spritePos:Vec2) {
  
  def move(direction:Vec2,delta:Double) {
    spritePos.x += spriteVel.x*direction.x*delta
    spritePos.y += spriteVel.y*direction.y*delta
  }
  def move(direction:Vec2) { spritePos += direction }
  
  def isOffScreen:Boolean = { if(spritePos.x > 1700 || spritePos.x < -200 || spritePos.y > 1200 || spritePos.y < -200) true else false}
  
  def display(gc:GraphicsContext,startingX:Int,startingY:Int,width:Double,height:Double) { gc.drawImage(spriteImg, startingX, startingY, width, height, spritePos.x, spritePos.y, width, height) }
  def display(gc:GraphicsContext) { gc.drawImage(spriteImg, spritePos.x, spritePos.y) }
  def display(gc:GraphicsContext,w:Double,h:Double) { gc.drawImage(spriteImg, spritePos.x, spritePos.y, w, h)}
  def display(gc:GraphicsContext,tempImg:Image) { gc.drawImage(tempImg, spritePos.x, spritePos.y, spriteImg.width.apply, spriteImg.height.apply)}
  def display(gc:GraphicsContext,tempImg:Image,boss:Boolean) { gc.drawImage(tempImg,spritePos.x-tempImg.width.apply.toInt/4,spritePos.y-tempImg.height.apply.toInt/4) }
  def display(gc:GraphicsContext,tempImg:Image,playerPos:Vec2,playerWidth:Double,playerHeight:Double) { gc.drawImage(tempImg, playerPos.x - playerWidth/2, playerPos.y) }
  
  def areCollidingRectvRect(other:Sprite):Boolean = {
    if(other.spritePos.x <= spritePos.x + spriteImg.width.apply &&
       other.spritePos.x >= spritePos.x &&
       other.spritePos.y <= spritePos.y + spriteImg.height.apply &&
       other.spritePos.y >= spritePos.y) true else false
  }
  
  def areCollidingShield(other:Sprite,rotation:Int,shield:Image,gc:GraphicsContext):Boolean = {
    val lengths = List(270,275,285,280,285,290,295,300,305,310,315,325,335,340,345,350)
    
    val vectorPoints = lengths.map(unit => {
      val theta = if(rotation >= 0 && rotation <= 180) unit - rotation
                  else unit - 180 - (180 - math.abs(rotation))
      val x = (spritePos.x + spriteImg.width.apply/2 ) + (80 * math.cos(theta * math.Pi / 180))
      val y = (spritePos.y + spriteImg.height.apply/2) + (80 * math.sin(theta * math.Pi / 180))
      val y2 = if(y >= spritePos.y + spriteImg.height.apply/2) y-2*(y - (spritePos.y + spriteImg.height.apply/2))
               else y+2*((spritePos.y + spriteImg.height.apply/2)-y)
      new Vec2(x,y2)
    })
    
    var theTruth = false
    
    vectorPoints.foreach(point => {
      if(point.x <= other.spritePos.x + other.spriteImg.width.apply &&
         point.x >= other.spritePos.x &&
         point.y <= other.spritePos.y + other.spriteImg.height.apply &&
         point.y >= other.spritePos.y) theTruth = true
    })
    
    theTruth
  }
}