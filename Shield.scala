package game2

import cs2.util.Vec2
import scalafx.scene.image.Image
import scalafx.scene.canvas.GraphicsContext
import scalafx.scene.SnapshotParameters
import scalafx.scene.image.ImageView
import scalafx.scene.paint.Color
import scalafx.scene.transform.Rotate


/**
 * @author eherbert
 */
class Shield(val img:Image,
             val vel:Vec2,
             val pos:Vec2,
             val playerWidth:Double,
             val playerHeight:Double) extends Sprite(img,
                                          vel,
                                          new Vec2(0,0)) {
  
  var rotation = 0
  
  def rotateClockwise {
    if(rotation < 180) rotation += 1
    else if(rotation == 180) rotation = -180
  }
  def rotateCounterClockwise {
    if(rotation > -180) rotation -= 1
    else if(rotation == -180) rotation = 180
  }
  
  def rotate(gc:GraphicsContext, playerPos:Vec2) {
    val r = new Rotate(rotation, playerPos.x + playerWidth/2, playerPos.y + playerHeight/2)
    gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
  }
  
  def display2(gc:GraphicsContext,playerPos:Vec2) {
    gc.save
    //gc.rotate(rotation)
    rotate(gc, playerPos)
    display(gc,img,playerPos,playerWidth,playerHeight)
    gc.restore
    
    /*val iv = new ImageView(img);
    iv.setRotate(rotation);
    val params = new SnapshotParameters();
    params.setFill(Color.TRANSPARENT);
    val rotatedImage = iv.snapshot(params, null);
    display(gc,rotatedImage,playerPos,playerWidth,playerHeight)*/
  }
  
  def copy:Shield = {
    val temp = new Shield(img, new Vec2(spriteVel.x,spriteVel.y),new Vec2(spritePos.x,spritePos.y),playerWidth.toDouble,playerHeight.toDouble)
    temp.rotation = rotation.toInt
    temp
  }
  
  def timeStep(gc:GraphicsContext, dPressed:Boolean, aPressed:Boolean,playerPos:Vec2) {
    if(dPressed) rotateClockwise
    if(aPressed) rotateCounterClockwise
  }
  
}