package edu.austral.ingsis.starships

import display.AlertBox
import Game
import config.Constants.*
import edu.austral.ingsis.starships.ui.*
import edu.austral.ingsis.starships.ui.ElementColliderType.*
import javafx.application.Application
import javafx.application.Application.launch
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.control.ChoiceBox
import javafx.scene.control.Label
import javafx.scene.input.KeyCode
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color.PURPLE
import javafx.stage.Popup
import javafx.stage.Stage
import model.Bullet
import model.Entity
import model.Starship
import model.enums.BulletType
import model.enums.Color
import model.enums.EntityShapeType
import model.enums.EntityType
import kotlin.system.exitProcess

fun main() {
    launch(Starships::class.java)
}

class Starships : Application() {
    private val imageResolver = CachedImageResolver(DefaultImageResolver())
    private val facade = ElementsViewFacade(imageResolver)
    private val keyTracker = KeyTracker()

    companion object {

        //Starships
        val STARSHIP_RED = ImageRef("playerShip1_red", 70.0, 70.0)
        val STARSHIP_BLUE = ImageRef("playerShip1_blue", 70.0, 70.0)
        val STARSHIP_GREEN = ImageRef("playerShip1_green", 70.0, 70.0)

        //Bullets
        val BULLET_RED = ImageRef("laserRed01", 70.0, 70.0)
        val BULLET_BLUE = ImageRef("laserBlue01", 70.0, 70.0)
        val BULLET_GREEN = ImageRef("laserGreen07", 70.0, 70.0)

        val BULLET_LASER_RED = ImageRef("laserRed14", 70.0, 70.0)
        val BULLET_LASER_BLUE = ImageRef("laserBlue14", 70.0, 70.0)
        val BULLET_LASER_GREEN = ImageRef("laserGreen06", 70.0, 70.0)

        val BULLET_ROCKET_RED = ImageRef("laserRed10", 70.0, 70.0)
        val BULLET_ROCKET_BLUE = ImageRef("laserBlue10", 70.0, 70.0)
        val BULLET_ROCKET_GREEN = ImageRef("laserGreen16", 70.0, 70.0)

        val BULLET_SUPA_ROCKET_RED = ImageRef("laserRed08", 70.0, 70.0)
        val BULLET_SUPA_ROCKET_BLUE = ImageRef("laserBlue08", 70.0, 70.0)
        val BULLET_SUPA_ROCKET_GREEN = ImageRef("laserGreen14", 70.0, 70.0)

        val BULLET_CUSTOM = ImageRef("laserCustom", 70.0, 70.0)

        //Asteroid
        val ASTEROID = ImageRef("meteorBrown_big1", 70.0, 70.0)

        val game = Game()
    }

    override fun start(primaryStage: Stage) {
        val pane = mainGameScene()
        val menu = menuScene(primaryStage, pane)

        facade.timeListenable.addEventListener(TimeListener(facade.elements, game, facade, this))
        facade.collisionsListenable.addEventListener(CollisionListener(game))
        keyTracker.keyPressedListenable.addEventListener(KeyPressedListener(game, this, primaryStage, pane, menu))

        keyTracker.scene = menu
        primaryStage.scene = menu
        primaryStage.height = GAME_HEIGHT
        primaryStage.width = GAME_WIDTH

        facade.start()
        keyTracker.start()
        primaryStage.show()
    }
    override fun stop() {
        facade.stop()
        keyTracker.stop()
        AlertBox.display("Lidl Starships", "Gracias por jugar starships")
        exitProcess(0)
    }

    fun adaptShape(shape : EntityShapeType) : ElementColliderType{
        return when(shape){
            EntityShapeType.RECTANGULAR -> Rectangular
            EntityShapeType.ELLIPTICAL -> Elliptical
            EntityShapeType.TRIANGULAR -> Triangular
        }
    }

    private fun mainGameScene(): StackPane {
        val pane = StackPane()
        val root = facade.view
        pane.children.addAll(root)
        root.id = "pane"
        return pane
    }

    private fun addGameObjects(){
        val gameObjects = game.entities
        for (gameObject in gameObjects){
            facade.elements[gameObject.id] = ElementModel(gameObject.id, gameObject.getxPosition(), gameObject.getyPosition(),
                gameObject.height, gameObject.width, gameObject.rotation, adaptShape(gameObject.shapeType), getImage(gameObject))
        }
    }

    fun getImage(gameObject: Entity) : ImageRef {
        if (gameObject.type == EntityType.STARSHIP) {
            if (gameObject is Starship) {
                return when (gameObject.color) {
                    Color.RED -> STARSHIP_RED
                    Color.BLUE -> STARSHIP_BLUE
                    else -> STARSHIP_GREEN
                }
            }
        }

        if (gameObject.type == EntityType.BULLET) {
            if (gameObject is Bullet) {
                val type = gameObject.bulletType;
                when (gameObject.color) {
                    Color.RED -> {
                        return when (type) {
                            BulletType.BULLET -> BULLET_RED
                            BulletType.LASER -> BULLET_LASER_RED
                            BulletType.ROCKET -> BULLET_ROCKET_RED
                            BulletType.SUPA_ROCKET -> BULLET_SUPA_ROCKET_RED
                            else -> BULLET_CUSTOM
                        }
                    }
                    Color.BLUE -> {
                        return when (type) {
                            BulletType.BULLET -> BULLET_BLUE
                            BulletType.LASER -> BULLET_LASER_BLUE
                            BulletType.ROCKET -> BULLET_ROCKET_BLUE
                            BulletType.SUPA_ROCKET -> BULLET_SUPA_ROCKET_BLUE
                            else -> BULLET_CUSTOM
                        }
                    }
                    else -> {
                        return when (type) {
                            BulletType.BULLET -> BULLET_GREEN
                            BulletType.LASER -> BULLET_LASER_GREEN
                            BulletType.ROCKET -> BULLET_ROCKET_GREEN
                            BulletType.SUPA_ROCKET -> BULLET_SUPA_ROCKET_GREEN
                            else -> BULLET_CUSTOM
                        }
                    }
                }
            }
        }
        return ASTEROID
    }


private fun menuScene(primaryStage: Stage, pane: StackPane): Scene {
    primaryStage.title = "Lidl Starships"
    val title = Label("Starships")
    title.textFill = PURPLE
    title.style = "-fx-font-family: VT323; -fx-font-size: 100;"

    val choiceBox = ChoiceBox<String>()

    val bulletTypesMap = game.gameConfiguration.bulletTypes

    for ((key,values) in bulletTypesMap.entries){
        choiceBox.items.add(key)
    }
    //Set a default bullet type
    choiceBox.value = "Choose a bullet type"

    val newGame = Label("New Game")
    newGame.textFill = javafx.scene.paint.Color.BLUE
    newGame.style = "-fx-font-family: VT323; -fx-font-size: 50"
    newGame.setOnMouseClicked {
        primaryStage.scene.root = pane

        game.start(false, choiceBox.value)
        addGameObjects()
    }

    newGame.setOnMouseEntered {
        newGame.textFill = javafx.scene.paint.Color.MEDIUMPURPLE
        newGame.cursor = Cursor.HAND
    }

    newGame.setOnMouseExited {
        newGame.textFill = javafx.scene.paint.Color.BLUE
    }

    val loadGame = Label("Load Game")
    loadGame.textFill = javafx.scene.paint.Color.BLUE
    loadGame.style = "-fx-font-family: VT323; -fx-font-size: 50;"
    loadGame.setOnMouseClicked {
        primaryStage.scene.root = pane
        game.start(true, "bulletType-1")
        addGameObjects()
    }
    loadGame.setOnMouseEntered {
        loadGame.textFill = javafx.scene.paint.Color.MEDIUMPURPLE
        loadGame.cursor = Cursor.HAND
    }

    loadGame.setOnMouseExited {
        loadGame.textFill = javafx.scene.paint.Color.BLUE
    }

    val newAndLoadGame = HBox(70.0)
    newAndLoadGame.alignment = Pos.CENTER
    newAndLoadGame.children.addAll(newGame, loadGame, choiceBox)

    val verticalLayout = VBox(50.0)
    verticalLayout.id = "menu"
    verticalLayout.alignment = Pos.CENTER
    verticalLayout.children.addAll(title, newAndLoadGame)

    val menu = Scene(verticalLayout)
    menu.stylesheets.add(this::class.java.classLoader.getResource("styles.css")?.toString())
    return menu
}


fun pauseScene(primaryStage: Stage, pane: StackPane, menu: Scene): Scene {
    val resume = Label("Resume")
    resume.textFill = javafx.scene.paint.Color.BLUE
    resume.style = "-fx-font-family: VT323; -fx-font-size: 50"
    resume.setOnMouseClicked {
        primaryStage.scene = menu
        primaryStage.scene.root = pane
        game.pauseOrResumeGame()
    }

    resume.setOnMouseEntered {
        resume.textFill = javafx.scene.paint.Color.MEDIUMPURPLE
        resume.cursor = Cursor.HAND
    }

    resume.setOnMouseExited {
        resume.textFill = javafx.scene.paint.Color.BLUE
    }
    var saved = false
    val saveGame = Label("Save game")
    saveGame.textFill = javafx.scene.paint.Color.BLUE
    saveGame.style = "-fx-font-family: VT323; -fx-font-size: 50;"
    saveGame.setOnMouseClicked {
        saveGame.textFill = javafx.scene.paint.Color.PURPLE
        game.saveGame()
        saved = true
    }
    saveGame.setOnMouseEntered {
        if (!saved){
            saveGame.textFill = javafx.scene.paint.Color.MEDIUMPURPLE
            saveGame.cursor = Cursor.HAND
        }
    }

    saveGame.setOnMouseExited {
        if (saved){
            saveGame.textFill = javafx.scene.paint.Color.PURPLE
        }
        else{
            saveGame.textFill = javafx.scene.paint.Color.BLUE
        }
    }

    val exitGame = Label("Exit game")
    exitGame.textFill = javafx.scene.paint.Color.BLUE
    exitGame.style = "-fx-font-family: VT323; -fx-font-size: 50;"
    exitGame.setOnMouseClicked {
        game.printLeaderBoard()
        stop()
    }
    exitGame.setOnMouseEntered {
        exitGame.textFill = javafx.scene.paint.Color.MEDIUMPURPLE
        exitGame.cursor = Cursor.HAND
    }

    exitGame.setOnMouseExited {
        exitGame.textFill = javafx.scene.paint.Color.BLUE
    }

    val verticalLayout = VBox(50.0)
    verticalLayout.id = "pause"
    verticalLayout.alignment = Pos.CENTER
    verticalLayout.children.addAll(
        resume,
        saveGame,
        exitGame
    )
    val pause = Scene(verticalLayout)
    pause.stylesheets.add(this::class.java.classLoader.getResource("styles.css")?.toString())
    return pause
}
    class TimeListener(
        private val elements: Map<String, ElementModel>,
        private val game: Game,
        private val facade: ElementsViewFacade,
        private val starships: Starships
    ) : EventListener<TimePassed> {
        override fun handle(event: TimePassed) {
            if(game.hasFinished()) {
                game.printLeaderBoard()
                starships.stop()
            }
            game.update()
            val gameObjects = game.entities ?: return;
            for (gameObject in gameObjects){
                val element = elements[gameObject.id]
                if (element != null) {
                    element.x.set(gameObject.getxPosition() + 70)
                    element.y.set(gameObject.getyPosition() + 70)
                    element.rotationInDegrees.set(gameObject.rotation + 180)
                    element.height.set(gameObject.height)
                    element.width.set(gameObject.width)
                }
                else{
                    facade.elements[gameObject.id] = ElementModel(gameObject.id, gameObject.getxPosition(), gameObject.getyPosition(), gameObject.height, gameObject.width, gameObject.rotation,
                        starships.adaptShape(gameObject.shapeType), starships.getImage(gameObject))
                }
            }
            val eliminations = game.eliminated;

            for (eliminated in eliminations){
                if (elements.containsKey(eliminated)){
                    facade.elements[eliminated] = null
                }
            }
        }
    }

class CollisionListener(private val game: Game) : EventListener<Collision> {
    override fun handle(event: Collision) {
        game.handleCollision(event.element1Id, event.element2Id)
    }
}

class KeyPressedListener( private val game: Game,
                          private val starships: Starships,
                          private val primaryStage: Stage,
                          private val pane: StackPane,
                          private val menu: Scene): EventListener<KeyPressed> {
                override fun handle(event: KeyPressed) {
                    val map = game.keyBoardConfig;
                    if (event.key == KeyCode.S && game.isPaused) game.saveGame()
                    when (event.key){
                        //First player
                        map["accelerate-1"] -> game.moveShip("starship-1", true)
                        map["stop-1"] -> game.moveShip("starship-1", false)
                        map["rotate-left-1"] -> game.rotateShip("starship-1", -SHIP_ROTATION_NORMAL)
                        map["rotate-right-1"] -> game.rotateShip("starship-1", SHIP_ROTATION_NORMAL)
                        map["shoot-1"] -> game.shoot("starship-1")
                        //Second player
                        map["accelerate-2"] -> game.moveShip("starship-2", true)
                        map["stop-2"] -> game.moveShip("starship-2", false)
                        map["rotate-left-2"] -> game.rotateShip("starship-2", -SHIP_ROTATION_NORMAL)
                        map["rotate-right-2"] -> game.rotateShip("starship-2", SHIP_ROTATION_NORMAL)
                        map["shoot-2"] -> game.shoot("starship-2")
                        //Third player
                        map["accelerate-2"] -> game.moveShip("starship-2", true)
                        map["stop-2"] -> game.moveShip("starship-2", false)
                        map["rotate-left-2"] -> game.rotateShip("starship-2", -SHIP_ROTATION_NORMAL)
                        map["rotate-right-2"] -> game.rotateShip("starship-2", SHIP_ROTATION_NORMAL)
                        map["shoot-2"] -> game.shoot("starship-2")
                        KeyCode.P -> {
                            game.pauseOrResumeGame()
                            if (game.isPaused){
                                primaryStage.scene = starships.pauseScene(primaryStage, pane, menu)
                            }
                        }
                        else -> {}
                    }
                }
            }
}