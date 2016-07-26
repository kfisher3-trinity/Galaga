package game2

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.paint.Color
import scalafx.scene.canvas.Canvas
import scalafx.scene.canvas.GraphicsContext
import scalafx.animation.AnimationTimer
import scalafx.scene.input.KeyCode
import scalafx.scene.input.KeyEvent
import scala.collection.mutable
import scalafx.scene.image.Image
import cs2.util.Vec2
import scalafx.scene.input.MouseEvent
import scalafx.event.ActionEvent
import scalafx.scene.control.Button
import scalafx.scene.layout.GridPane
import scalafx.scene.control.Label
import scalafx.scene.shape.Rectangle
import scalafx.scene.layout.VBox
import scalafx.scene.media._
import java.io.File
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import java.net._
import java.io._
import java.lang.Thread
import game2.Images._

/**
 * @author eherbert
 */

object SpaceGameApp {
  var gameStates = mutable.Stack[GameState]()

  val app = new JFXApp {
    stage = new JFXApp.PrimaryStage {
      title = "hi"
      scene = new Scene(1500, 800) {
        fill = Color.Black

        var upPressed = false
        var downPressed = false
        var leftPressed = false
        var rightPressed = false
        var wPressed = false
        var sPressed = false
        var aPressed = false
        var dPressed = false
        var shootLazer = false
        var showStartScreen = true
        var showFinalScreen = false
        var rewind = false
        var bossTrip = false
        var bossPresent = false
        var allowedToRewind = false
        var activateMedia = false
        var onePlayer = true
        var twoPlayer = false

        var rewindTimer = 59.0
        var pendingBulletTimer = 20
        var pendingEnemyTimer = 20
        var pendingBossTimer = 30
        var randomPowerUpThreshold = 0.1

        val canvas = new Canvas(1500, 800)
        val gc = canvas.graphicsContext2D

        val gridPane = new GridPane
        //gridPane.layoutX = scene.width.apply - gridPane.width
        //gridPane.layoutY = 20
        gridPane.hgap = 20
        gridPane.vgap = 10
        val livesLabel = new Label("Lives:")
        gridPane.add(livesLabel, 0, 0)
        val livesNumber = new Label("3")
        gridPane.add(livesNumber, 0, 1)
        val scoreLabel = new Label("Score:")
        gridPane.add(scoreLabel, 1, 0)
        val scoreNumber = new Label("0")
        gridPane.add(scoreNumber, 1, 1)
        val rewindLabel = new Label("Rewind:")
        gridPane.add(rewindLabel, 2, 0)
        val rewindRect = new Rectangle
        rewindRect.height = 20
        rewindRect.width = 64
        rewindRect.fill = Color.Black
        rewindRect.stroke
        gridPane.add(rewindRect, 2, 1)
        val rewindRect2 = new Rectangle
        rewindRect2.height = 16
        rewindRect2.width = 0 //maxis60
        rewindRect2.layoutX = 1322
        rewindRect2.layoutY = 48
        rewindRect2.fill = Color.Green
        gridPane.layoutX = 1200
        gridPane.layoutY = 20

        val startText = new Label("You are a Diet Coke can who has recently realized her dreams of becoming an actress in Hollywood.")
        startText.textFill = Color.White
        startText.layoutX = 200
        startText.layoutY = 200
        val startText2 = new Label("You go to Hollywood to follow your dreams, only to find out that there are many other Pepsi actors trying to land the same gigs.")
        startText2.textFill = Color.White
        startText2.layoutX = 200
        startText2.layoutY = 230
        val startText3 = new Label("You must take out the other actors in order to get the best acting roles.")
        startText3.textFill = Color.White
        startText3.layoutX = 200
        startText3.layoutY = 260
        val startText4 = new Label("This is your journey to making it in the Big City.")
        startText4.textFill = Color.White
        startText4.layoutX = 200
        startText4.layoutY = 290
        val startButton = new Button("Start Game")
        startButton.layoutX = 750 - startButton.width.apply
        startButton.layoutY = 400 - startButton.height.apply
        startButton.onAction = (e: ActionEvent) => {
          showStartScreen = false
          showFinalScreen = false
          content = List(canvas, gridPane, rewindRect2)
          fill = Color.LightBlue
          gameTimer.start
          songOneMediaPlayer.play
        }
        val onePlayerButton = new Button("One Player?")
        onePlayerButton.layoutX = 300 - onePlayerButton.width.apply
        onePlayerButton.layoutY = 400 - onePlayerButton.height.apply
        onePlayerButton.onAction = (e: ActionEvent) => {
          onePlayer = true
          twoPlayer = false
          shieldBuffer.clear()
        }
        val twoPlayerButton = new Button("Two Player?")
        twoPlayerButton.layoutX = 525 - twoPlayerButton.width.apply
        twoPlayerButton.layoutY = 400 - twoPlayerButton.height.apply
        twoPlayerButton.onAction = (e: ActionEvent) => {
          onePlayer = false
          twoPlayer = true
          shieldBuffer.prepend(new Shield(shield, new Vec2(0, 0), new Vec2(player.spritePos.x + player.spriteImg.width.apply / 2, player.spritePos.y + player.spriteImg.height.apply / 2), player.spriteImg.width.apply, player.spriteImg.height.apply))
        }

        val playAgainButton = new Button("Play Again")
        playAgainButton.layoutX = 750 - playAgainButton.width.apply
        playAgainButton.layoutY = 400 - playAgainButton.height.apply
        playAgainButton.onAction = (e: ActionEvent) => {
          player.spritePos = new Vec2(1400, 600)
          player.currentLives = 3.0
          player.currentScore = 0.0
          swarmBuffer.clear()
          bossBuffer.clear()

          swarmBuffer.prepend(new EnemySwarm(5,
            4,
            new Vec2(200, 200),
            pepsi,
            new Vec2(100, 100),
            pepsiTop,
            10,
            200.0,
            fire))
          val background = new Background(hollywood, List(dietCokeLogo, pepsiLogo))
          showStartScreen = false
          showFinalScreen = false
          rewindTimer = 59.0
          content = List(canvas, gridPane)
          gameTimer.start()
          songOneMediaPlayer.play()
        }

        content = List(startButton, onePlayerButton, twoPlayerButton, startText, startText2, startText3, startText4)

        var player = new Player(dietCoke, new Vec2(200, 200), new Vec2(500, 500), dietCokeTop, 0, 3, playerAnimationBuffer, fire)
        var shieldBuffer = mutable.Buffer[Shield]()
        var background = new Background(hollywood, List(dietCokeLogo, pepsiLogo))

        var swarmBuffer = mutable.Buffer[EnemySwarm]()
        var pendingSwarmBuffer = mutable.Map[EnemySwarm, Double]()
        var deadSwarmBuffer = mutable.Buffer[EnemySwarm]()

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

        //var gameStates = mutable.Stack[GameState]()

        onKeyPressed = (e: KeyEvent) => {
          e.code match {
            case KeyCode.UP => { upPressed = true }
            case KeyCode.W => { wPressed = true }
            case KeyCode.LEFT => { leftPressed = true }
            case KeyCode.A => { aPressed = true }
            case KeyCode.RIGHT => { rightPressed = true }
            case KeyCode.D => { dPressed = true }
            case KeyCode.Down => { downPressed = true }
            case KeyCode.S => { sPressed = true }
            case KeyCode.R => { rewind = true }
            case KeyCode.SPACE => { shootLazer = true }
            case _ =>
          }
        }

        onKeyReleased = (e: KeyEvent) => {
          e.code match {
            case KeyCode.UP => { upPressed = false }
            case KeyCode.W => { wPressed = false }
            case KeyCode.LEFT => { leftPressed = false }
            case KeyCode.A => { aPressed = false }
            case KeyCode.RIGHT => { rightPressed = false }
            case KeyCode.D => { dPressed = false }
            case KeyCode.Down => { downPressed = false }
            case KeyCode.S => { sPressed = false }
            case KeyCode.R => { rewind = false }
            case KeyCode.SPACE => { shootLazer = false }
            case _ =>
          }
        }

        onMouseClicked = (e: MouseEvent) => {
          if (!shootLazer) {
            songTwoMediaPlayer.play
            player.shoot(e.x, e.y, leftPressed, rightPressed, upPressed, downPressed)
            songTwoMediaPlayer = new MediaPlayer(sound2)
          } else {
            songThreeMediaPlayer.play
            player.shootLazer(e.x, e.y, leftPressed, rightPressed, upPressed, downPressed)
            songThreeMediaPlayer = new MediaPlayer(sound3)
          }
        }

        def checkIntersections {
          try {
            player.lazerBuffer.foreach(lazer => {
              lazer.blocksBuffer.foreach(block => {
                try {
                  if (!shieldBuffer.isEmpty && player.areCollidingShield(block, shieldBuffer(0).rotation, shield, gc)) {
                    player.lazerBuffer -= lazer
                  }
                } catch {
                  case e: NullPointerException => println("NullPointerException")
                }
                swarmBuffer.foreach(swarm => {
                  swarm.bullets.foreach(bullet => {
                    if (bullet.areCollidingRectvRect(block)) {
                      lazer.pendingBlocksBuffer += (block -> 0)
                      swarm.pendingBullets += (bullet -> 0)
                    }
                  })
                  swarm.enemies.foreach(enemy => {
                    if (enemy.areCollidingRectvRect(block)) {
                      lazer.pendingBlocksBuffer += (block -> 0)
                      swarm.pendingEnemies += (enemy -> 0)
                      player.currentScore += enemy.pointValue
                    }
                    enemy.bullets.foreach(eBullet => {
                      if (eBullet.areCollidingRectvRect(block)) {
                        enemy.pendingBullets += (eBullet -> 0)
                        lazer.pendingBlocksBuffer += (block -> 0)
                      }
                    })
                  })
                })
              })
            })
            player.bullets.foreach(pBullets => { //for each bullet in player
              if (!shieldBuffer.isEmpty && player.areCollidingShield(pBullets, shieldBuffer(0).rotation, shield, gc)) player.pendingBullets += (pBullets -> 0)
              bossBuffer.foreach(boss => {
                if (boss.areCollidingRectvRect(pBullets)) {
                  boss.bossCurrentLives -= 1
                  player.pendingBullets += (pBullets -> 0)
                }
                boss.bullets.foreach(bullet => {
                  if (bullet.areCollidingRectvRect(pBullets)) {
                    boss.pendingBullets += (bullet -> 0)
                    player.pendingBullets += (pBullets -> 0)
                  }
                  if (bullet.areCollidingRectvRect(player)) {
                    player.currentLives -= 0.1
                    boss.pendingBullets += (bullet -> 0)
                  }
                  if (!shieldBuffer.isEmpty && player.areCollidingShield(bullet, shieldBuffer(0).rotation, shield, gc)) boss.pendingBullets += (bullet -> 0)
                })
                if (boss.areCollidingRectvRect(player)) {
                  boss.currentLives -= 1
                  player.currentLives -= 0.1
                }
              })
              swarmBuffer.foreach(swarm => { //for each enemy swarm
                swarm.enemies.foreach(enemy => { //for each enemy in each swarm
                  if (enemy.areCollidingRectvRect(pBullets)) { //if a player bullet hits an enemy
                    enemy.bullets.foreach(eBullet => swarm.bullets.prepend(eBullet)) //hit enemy's bullets get added to swarms bullet list
                    swarm.pendingEnemies += (enemy -> 0)
                    if (math.random < randomPowerUpThreshold) createNewPowerUp(enemy.spritePos)
                    player.pendingBullets += (pBullets -> 0) //add hit bullet to player dead bullet list
                    player.currentScore += enemy.pointValue
                  }
                  enemy.bullets.foreach(eBullets => { //for each bullet in each enemy
                    if (eBullets.areCollidingRectvRect(pBullets)) { //if a player bullet hits and enemy bullet
                      enemy.pendingBullets += (eBullets -> 0) //adds hit bullet to enemy dead bullet list
                      player.pendingBullets += (pBullets -> 0) //add hit bullet to player dead bullet list
                    }
                    //if(player.areCollidingShield(eBullets, shieldBuffer(0).rotation, shield, gc)) enemy.pendingBullets += (eBullets -> 0)
                  })
                })
              })
            })
            swarmBuffer.foreach(swarm => { //for each enemy swarm
              swarm.enemies.foreach(enemy => { //for each enemy in swarm
                if (player.areCollidingRectvRect(enemy)) {
                  enemy.bullets.foreach(eBullet => swarm.bullets.prepend(eBullet))
                  swarm.pendingEnemies += (enemy -> 0)
                  if (math.random < randomPowerUpThreshold) createNewPowerUp(enemy.spritePos)
                  player.currentLives -= 0.1
                }
                enemy.bullets.foreach(eBullets => { //for each bullet in enemy
                  if (player.areCollidingRectvRect(eBullets)) {
                    enemy.pendingBullets += (eBullets -> 0)
                    player.currentLives -= 0.1
                  }
                  if (!shieldBuffer.isEmpty && player.areCollidingShield(eBullets, shieldBuffer(0).rotation, shield, gc)) enemy.pendingBullets += (eBullets -> 0)
                })
              })
            })
            lifePowerUpBuffer.foreach(powerUp => {
              if (player.areCollidingRectvRect(powerUp)) {
                deadLifePowerUpBuffer.prepend(powerUp)
                player.currentLives += powerUp.value
              }
            })
            rewindPowerUpBuffer.foreach(powerUp => {
              if (player.areCollidingRectvRect(powerUp)) {
                deadRewindPowerUpBuffer.prepend(powerUp)
                updateRewindStats(rewindTimer - 1)
              }
            })
            speedPowerUpBuffer.foreach(powerUp => {
              if (player.areCollidingRectvRect(powerUp)) {
                deadSpeedPowerUpBuffer.prepend(powerUp)
                player.spriteVel = new Vec2(player.spriteVel.x + powerUp.value, player.spriteVel.y + powerUp.value)
              }
            })
            pointPowerUpBuffer.foreach(powerUp => {
              if (player.areCollidingRectvRect(powerUp)) {
                deadPointPowerUpBuffer.prepend(powerUp)
                player.currentScore += 50
              }
            })
            bossBuffer.foreach(boss => {
              boss.bullets.foreach(bullet => {
                if (!shieldBuffer.isEmpty && player.areCollidingShield(bullet, shieldBuffer(0).rotation, shield, gc)) boss.pendingBullets += (bullet -> 0)
              })
            })
          } catch { case e: NullPointerException => println("checkIntersections -> NullPointerException") }
        }
        def removePendingPartsFromPartsAndMovePendingPartsToDeadParts {
          try {
            swarmBuffer.foreach(swarm => {
              swarm.pendingBullets.foreach(bullet => {
                if (bullet._2 > pendingBulletTimer) { swarm.deadBullets.prepend(bullet._1) }
                else if (bullet._2 == 0) { swarm.deadBullets -= bullet._1 }
              })
              swarm.pendingEnemies.foreach(enemy => {
                if (enemy._2 > pendingEnemyTimer) { swarm.deadEnemies.prepend(enemy._1) }
                else if (enemy._2 == 0) { swarm.enemies -= enemy._1 }
              })
              swarm.enemies.foreach(enemy => {
                enemy.pendingBullets.foreach(bullet => {
                  if (bullet._2 > pendingBulletTimer) { enemy.deadBullets.prepend(bullet._1) }
                  else if (bullet._2 == 0) { enemy.bullets -= bullet._1 }
                })
              })
            })
            player.pendingBullets.foreach(bullet => {
              if (bullet._2 > pendingBulletTimer) { player.deadBullets.prepend(bullet._1) }
              else if (bullet._2 == 0) { player.bullets -= bullet._1 }
            })
            player.lazerBuffer.foreach(lazer => {
              lazer.pendingBlocksBuffer.foreach(block => {
                if (block._2 > pendingBulletTimer) lazer.deadBlocksBuffer.prepend(block._1)
                else if (block._2 == 0) lazer.blocksBuffer -= block._1
              })
            })
            bossBuffer.foreach(boss => {
              boss.pendingBullets.foreach(bullet => {
                if (bullet._2 > pendingBulletTimer) { boss.deadBullets.prepend(bullet._1) }
                else if (bullet._2 == 0) { boss.bullets -= bullet._1 }
              })
            })
            pendingBossBuffer.foreach(boss => {
              if (boss._2 > pendingBossTimer) { deadBossBuffer.prepend(boss._1) }
              else if (boss._2 == 0) { bossBuffer -= boss._1 }
            })
            pendingSwarmBuffer.foreach(swarm => {
              deadSwarmBuffer.prepend(swarm._1)
              swarmBuffer -= swarm._1
            })
          } catch { case e: NullPointerException => println("movePendingPartsToDeadParts -> NullPointerException") }
        }
        def removeDeadPartsFromPendingParts {
          try {
            swarmBuffer.foreach(swarm => {
              swarm.deadBullets.foreach(bullet => swarm.pendingBullets -= bullet)
              swarm.deadEnemies.foreach(enemy => swarm.pendingEnemies -= enemy)
              swarm.enemies.foreach(enemy => { enemy.deadBullets.foreach(bullet => enemy.pendingBullets -= bullet) })
            })
            player.deadBullets.foreach(bullet => player.pendingBullets -= bullet)
            player.lazerBuffer.foreach(lazer => {
              lazer.deadBlocksBuffer.foreach(block => lazer.pendingBlocksBuffer -= block)
            })
            bossBuffer.foreach(boss => { boss.deadBullets.foreach(bullet => boss.pendingBullets -= bullet) })
            deadBossBuffer.foreach(boss => pendingBossBuffer -= boss)
            deadSwarmBuffer.foreach(swarm => pendingSwarmBuffer -= swarm)
            deadLifePowerUpBuffer.foreach(powerUp => lifePowerUpBuffer -= powerUp)
            deadRewindPowerUpBuffer.foreach(powerUp => rewindPowerUpBuffer -= powerUp)
            deadSpeedPowerUpBuffer.foreach(powerUp => speedPowerUpBuffer -= powerUp)
            deadPointPowerUpBuffer.foreach(powerUp => pointPowerUpBuffer -= powerUp)
          } catch { case e: NullPointerException => println("removeDeadPartsFromPendingParts -> NullPointerException") }
        }
        def updatePendingParts {
          swarmBuffer.foreach(swarm => {
            swarm.pendingBullets = swarm.pendingBullets.map { case (x, y) => (x, y + 0.5) }
            swarm.pendingEnemies = swarm.pendingEnemies.map { case (x, y) => (x, y + 0.5) }
            swarm.enemies.foreach(enemy => {
              enemy.pendingBullets = enemy.pendingBullets.map { case (x, y) => (x, y + 0.5) }
            })
          })
          bossBuffer.foreach(boss => {
            boss.pendingBullets = boss.pendingBullets.map { case (x, y) => (x, y + 0.5) }
          })
          pendingBossBuffer = pendingBossBuffer.map { case (x, y) => (x, y + 0.5) }
          player.pendingBullets = player.pendingBullets.map { case (x, y) => (x, y + 0.5) }
          player.lazerBuffer.foreach(lazer => {
            lazer.pendingBlocksBuffer = lazer.pendingBlocksBuffer.map { case (x, y) => (x, y + 0.5) }
          })
        }
        def clearDeadParts {
          player.deadBullets.clear()
          player.lazerBuffer.foreach(lazer => lazer.deadBlocksBuffer.clear())
          deadSwarmBuffer.clear()
          deadBossBuffer.clear()
          swarmBuffer.foreach(swarm => {
            swarm.deadBullets.clear()
            swarm.enemies.foreach(enemy => enemy.deadBullets.clear())
          })
          bossBuffer.foreach(boss => boss.deadBullets.clear())
          deadLifePowerUpBuffer.clear()
          deadRewindPowerUpBuffer.clear()
          deadSpeedPowerUpBuffer.clear()
          deadPointPowerUpBuffer.clear()
        }
        def moveOffScreenBulletsToPendingBullets {
          player.bullets.foreach(bullet => if (bullet.isOffScreen) player.pendingBullets += (bullet -> 0))
          player.lazerBuffer.foreach(lazer => {
            lazer.blocksBuffer.foreach(block => if (block.isOffScreen) lazer.pendingBlocksBuffer += (block -> 0))
          })
          swarmBuffer.foreach(swarm => {
            swarm.bullets.foreach(bullet => if (bullet.isOffScreen) swarm.pendingBullets += (bullet -> 0))
            swarm.enemies.foreach(enemy => enemy.bullets.foreach(bullet => if (bullet.isOffScreen) enemy.pendingBullets += (bullet -> 0)))
          })
          bossBuffer.foreach(boss => {
            boss.bullets.foreach(bullet => if (bullet.isOffScreen) boss.pendingBullets += (bullet -> 0))
          })
        }
        def createNewBoss: Boss = {
          val temp = new Boss(rc, new Vec2(200, 200), new Vec2(util.Random.nextInt(1200), util.Random.nextInt(600)), pepsiTop, 6, bossAnimationBuffer, 200, fire)
          if (temp.areCollidingRectvRect(player)) createNewBoss
          temp
        }
        def createNewEnemySwarm: EnemySwarm = {
          val temp2 = new EnemySwarm(util.Random.nextInt(5) + 1,
            util.Random.nextInt(5) + 1,
            new Vec2(util.Random.nextInt(1200), util.Random.nextInt(500)),
            pepsi,
            new Vec2(100, 100),
            pepsiTop,
            10,
            200.0,
            fire)
          for (i <- 0 until temp2.nRows; j <- 0 until temp2.nCols) {
            val temp = new Vec2(i * (temp2.enemyImg.width.apply + 10) + temp2.initPos.x, j * (temp2.enemyImg.height.apply + 10) + temp2.initPos.y)
            temp2.enemies.prepend(new Enemy(temp2.enemyImg, temp2.enemyVel, temp, temp2.bulletImg, temp2.enemyPointValue, fire))
          }
          var free = true
          temp2.enemies.foreach(enemy => if (enemy.areCollidingRectvRect(player)) free = false)
          if (!free) createNewEnemySwarm else temp2
        }
        def createNewPowerUp(p: Vec2) = {
          val rand = math.random
          if (rand <= 0.25) lifePowerUpBuffer.prepend(new LifePowerUp(board, new Vec2(p.x, p.y), math.random, 1))
          else if (rand > 0.25 && rand <= 0.5) rewindPowerUpBuffer.prepend(new RewindPowerUp(lens, new Vec2(p.x, p.y), 1))
          else if (rand > 0.5 && rand <= 0.75) speedPowerUpBuffer.prepend(new SpeedPowerUp(paper, new Vec2(p.x, p.y), 10, 1))
          else pointPowerUpBuffer.prepend(new PointPowerUp(star, new Vec2(p.x, p.y), 1))
        }
        def checkSwarmBuffer {
          try {
            swarmBuffer.foreach(swarm => { if (swarm.enemies.isEmpty) pendingSwarmBuffer += (swarm -> 0) })
          } catch { case e: NullPointerException => println("checkSwarmBuffer:swarmBuffer.foreach -> NullPointerException") }
          if (swarmBuffer.isEmpty) { for (i <- 0 until util.Random.nextInt(3)) swarmBuffer.prepend(createNewEnemySwarm) }
        }
        def checkBossBuffer {
          try {
            bossBuffer.foreach(boss => if (boss.currentLives < 1) {
              player.currentScore += 500
              pendingBossBuffer += (boss -> 0)
            })
          } catch { case e: NullPointerException => println("checkBossBuffer:bossBuffer.foreach -> NullPointerException") }
          if (bossBuffer.isEmpty && math.random < 0.0001) bossBuffer.prepend(createNewBoss)
        }
        def createNewPlayerVec2(leftPressed: Boolean, rightPressed: Boolean, upPressed: Boolean, downPressed: Boolean): Vec2 = {
          var x = 0
          var y = 0
          if (leftPressed == true) x += 1
          if (rightPressed == true) x -= 1
          if (downPressed == true) y -= 1
          if (upPressed == true) y += 1
          new Vec2(x, y)
        }
        def resetPlayerUponLifeDrop {
          if (player.currentLives < 2.1 && player.currentLives > 1.9) {
            player.spritePos = new Vec2(1400, 600)
            player.currentLives -= 0.001
          } else if (player.currentLives < 1.1 && player.currentLives > 0.9) {
            player.spritePos = new Vec2(1400, 600)
            player.currentLives -= 0.001
          } else if (player.currentLives < 0.1) showFinalScreen = true
        }
        def updateRewindStats(n: Double) {
          if (n > 0 && rewindTimer > 1) rewindTimer -= n
          else if (n < 0 && rewindTimer < 60) rewindTimer -= n
          if (rewindTimer <= 1) allowedToRewind = true
          else if (rewindTimer >= 60) allowedToRewind = false
          rewindRect2.width = 60 - rewindTimer
        }
        def createNewGameState(delta: Double): GameState = {
          val temp = new GameState

          temp.showStartScreen = if (showStartScreen) true else false
          temp.showFinalScreen = if (showFinalScreen) true else false
          temp.rewind = if (rewind) true else false
          temp.allowedToRewind = if (allowedToRewind) true else false

          temp.playerBuffer.prepend(player.copy)
          shieldBuffer.foreach(shield => temp.shieldBuffer.prepend(shield.copy))

          swarmBuffer.foreach(swarm => temp.swarmBuffer.prepend(swarm.copy))
          pendingSwarmBuffer.foreach(swarm => temp.pendingSwarmBuffer += (swarm._1.copy -> swarm._2.toInt))
          deadSwarmBuffer.foreach(swarm => temp.deadSwarmBuffer.prepend(swarm.copy))

          temp.background.prepend(background.copy)

          bossBuffer.foreach(boss => temp.bossBuffer.prepend(boss.copy))
          pendingBossBuffer.foreach(boss => temp.pendingBossBuffer += (boss._1.copy -> boss._2.toInt))
          deadBossBuffer.foreach(boss => temp.deadBossBuffer.prepend(boss.copy))

          lifePowerUpBuffer.foreach(powerUp => temp.lifePowerUpBuffer.prepend(powerUp.copy))
          rewindPowerUpBuffer.foreach(powerUp => temp.rewindPowerUpBuffer.prepend(powerUp.copy))
          speedPowerUpBuffer.foreach(powerUp => temp.speedPowerUpBuffer.prepend(powerUp.copy))
          pointPowerUpBuffer.foreach(powerUp => temp.pointPowerUpBuffer.prepend(powerUp.copy))
          deadLifePowerUpBuffer.foreach(powerUp => temp.deadLifePowerUpBuffer.prepend(powerUp.copy))
          deadRewindPowerUpBuffer.foreach(powerUp => temp.deadRewindPowerUpBuffer.prepend(powerUp.copy))
          deadSpeedPowerUpBuffer.foreach(powerUp => temp.deadSpeedPowerUpBuffer.prepend(powerUp.copy))
          deadPointPowerUpBuffer.foreach(powerUp => temp.deadPointPowerUpBuffer.prepend(powerUp.copy))

          temp.leftPressed = if (leftPressed) true else false
          temp.rightPressed = if (rightPressed) true else false
          temp.upPressed = if (upPressed) true else false
          temp.downPressed = if (downPressed) true else false

          temp.delta = delta.toDouble
          temp
        }
        def updateValuesToGameStateValues {
          shieldBuffer.clear()
          swarmBuffer.clear()
          pendingSwarmBuffer.clear()
          deadSwarmBuffer.clear()

          background.spawns.clear()

          bossBuffer.clear()
          pendingBossBuffer.clear()
          deadBossBuffer.clear()

          lifePowerUpBuffer.clear()
          rewindPowerUpBuffer.clear()
          speedPowerUpBuffer.clear()
          pointPowerUpBuffer.clear()
          deadLifePowerUpBuffer.clear()
          deadRewindPowerUpBuffer.clear()
          deadSpeedPowerUpBuffer.clear()
          deadPointPowerUpBuffer.clear()

          val temp = gameStates.pop()
          player = temp.playerBuffer(0).copy
          temp.shieldBuffer.foreach(shield => shieldBuffer.prepend(shield.copy))

          temp.swarmBuffer.foreach(swarm => swarmBuffer.prepend(swarm.copy))
          temp.pendingSwarmBuffer.foreach(swarm => pendingSwarmBuffer += (swarm._1.copy -> swarm._2.toInt))
          temp.deadSwarmBuffer.foreach(swarm => deadSwarmBuffer.prepend(swarm.copy))

          background = temp.background(0).copy

          temp.bossBuffer.foreach(boss => bossBuffer.prepend(boss.copy))
          temp.pendingBossBuffer.foreach(boss => pendingBossBuffer += (boss._1.copy -> boss._2.toInt))
          temp.deadBossBuffer.foreach(boss => deadBossBuffer.prepend(boss.copy))

          temp.lifePowerUpBuffer.foreach(powerUp => lifePowerUpBuffer.prepend(powerUp.copy))
          temp.rewindPowerUpBuffer.foreach(powerUp => rewindPowerUpBuffer.prepend(powerUp.copy))
          temp.speedPowerUpBuffer.foreach(powerUp => speedPowerUpBuffer.prepend(powerUp.copy))
          temp.pointPowerUpBuffer.foreach(powerUp => pointPowerUpBuffer.prepend(powerUp.copy))
          temp.deadLifePowerUpBuffer.foreach(powerUp => deadLifePowerUpBuffer.prepend(powerUp.copy))
          temp.deadRewindPowerUpBuffer.foreach(powerUp => deadRewindPowerUpBuffer.prepend(powerUp.copy))
          temp.deadSpeedPowerUpBuffer.foreach(powerUp => deadSpeedPowerUpBuffer.prepend(powerUp.copy))
          temp.deadPointPowerUpBuffer.foreach(powerUp => deadPointPowerUpBuffer.prepend(powerUp.copy))
        }

        var lastTime = 0L
        val gameTimer: AnimationTimer = AnimationTimer(t => {
          if (lastTime > 0) {
            val delta = (t - lastTime) / 1e9
            if (showStartScreen && !showFinalScreen) {}
            else if (!showStartScreen && !showFinalScreen) {
              if (!rewind || !allowedToRewind) {

                if (player.currentLives < 0) showFinalScreen = true

                clearDeadParts
                checkSwarmBuffer
                checkBossBuffer
                checkIntersections
                moveOffScreenBulletsToPendingBullets
                removePendingPartsFromPartsAndMovePendingPartsToDeadParts
                removeDeadPartsFromPendingParts
                updatePendingParts
                //resetPlayerUponLifeDrop
                updateRewindStats(0.005)

                background.timeStep(gc)
                swarmBuffer.foreach(swarm => swarm.timeStep(gc, delta, player.spritePos.x + (player.img.width / 2).toInt, player.spritePos.y + (player.img.height / 2).toInt))
                bossBuffer.foreach(boss => { boss.timeStep(gc, delta, player.spritePos.x + (player.img.width / 2).toInt, player.spritePos.y + (player.img.height / 2).toInt, createNewPlayerVec2(leftPressed, rightPressed, upPressed, downPressed)) })
                shieldBuffer.foreach(shield => shield.timeStep(gc, dPressed, aPressed, new Vec2(player.spritePos.x, player.spritePos.y)))
                player.timeStep(gc, leftPressed, rightPressed, upPressed, downPressed, delta)

                gameStates.push(createNewGameState(delta))
              } else if (gameStates.length > 1 && allowedToRewind) {
                updateValuesToGameStateValues
                updateRewindStats(-0.06)
              }
              gc.clearRect(0, 0, 1500, 800)
              gameStates(0).display2(gc)
              scoreNumber.text = player.currentScore.toString
              livesNumber.text = player.currentLives.toString.take(3)
            } else if (!showStartScreen && showFinalScreen) { content = List(playAgainButton) }
          }
          lastTime = t
        })
      }
    }
  }

  def main(args: Array[String]) { app.main(args) }
}