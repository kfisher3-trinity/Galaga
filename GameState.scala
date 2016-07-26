package game2

import scala.collection.mutable
import scalafx.scene.canvas.GraphicsContext

/**
 * @author eherbert
 */
class GameState {
  var showStartScreen = false
  var showFinalScreen = false
  var rewind = false
  var allowedToRewind = false
  
  var playerBuffer = mutable.Buffer[Player]()
  var shieldBuffer = mutable.Buffer[Shield]()

  var swarmBuffer = mutable.Buffer[EnemySwarm]()
  var pendingSwarmBuffer = mutable.Map[EnemySwarm, Double]()
  var deadSwarmBuffer = mutable.Buffer[EnemySwarm]()

  var background = mutable.Buffer[Background]()

  var bossBuffer = mutable.Buffer[Boss]()
  var pendingBossBuffer = mutable.Map[Boss, Double]()
  var deadBossBuffer = mutable.Buffer[Boss]()

  var lifePowerUpBuffer = mutable.Buffer[LifePowerUp]()
  var speedPowerUpBuffer = mutable.Buffer[SpeedPowerUp]()
  var rewindPowerUpBuffer = mutable.Buffer[RewindPowerUp]()
  var pointPowerUpBuffer = mutable.Buffer[PointPowerUp]()
  var deadLifePowerUpBuffer = mutable.Buffer[LifePowerUp]()
  var deadSpeedPowerUpBuffer = mutable.Buffer[SpeedPowerUp]()
  var deadRewindPowerUpBuffer = mutable.Buffer[RewindPowerUp]()
  var deadPointPowerUpBuffer = mutable.Buffer[PointPowerUp]()

  var leftPressed = false
  var rightPressed = false
  var upPressed = false
  var downPressed = false

  var delta = 0.0

  def display2(gc: GraphicsContext) {
    background(0).display2(gc)

    swarmBuffer.foreach(swarm => swarm.display2(gc))

    bossBuffer.foreach(boss => boss.display2(gc))
    pendingBossBuffer.foreach(boss => boss._1.display(gc, boss._1.bossAnimations(5), true))
    
    lifePowerUpBuffer.foreach(powerUp => powerUp.display2(gc))
    speedPowerUpBuffer.foreach(powerUp => powerUp.display2(gc))
    rewindPowerUpBuffer.foreach(powerUp => powerUp.display2(gc))
    pointPowerUpBuffer.foreach(powerUp => powerUp.display2(gc))

    playerBuffer(0).display2(gc, leftPressed, rightPressed, upPressed, downPressed)
    shieldBuffer.foreach(shield => { shield.display2(gc, playerBuffer(0).spritePos) })
  }
}