package upc.smtpra.perfeet.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.*
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import upc.smtpra.perfeet.Constantes
import upc.smtpra.perfeet.R
import com.google.ar.sceneform.rendering.Color as arColor
import java.util.Objects
import kotlin.math.pow
import kotlin.math.sqrt

class Medicion : AppCompatActivity(), Scene.OnUpdateListener {

    private val MIN_OPENGL_VERSION = 3.0
    private val TAG: String = Medicion::class.java.getSimpleName()

    private var arFragment: ArFragment? = null

    private var distanceModeTextView: TextView? = null
    private lateinit var pointTextView: TextView

    private lateinit var arrow1UpLinearLayout: LinearLayout
    private lateinit var arrow1DownLinearLayout: LinearLayout
    private lateinit var arrow1UpView: ImageView
    private lateinit var arrow1DownView: ImageView
    private lateinit var arrow1UpRenderable: Renderable
    private lateinit var arrow1DownRenderable: Renderable

    private lateinit var arrow10UpLinearLayout: LinearLayout
    private lateinit var arrow10DownLinearLayout: LinearLayout
    private lateinit var arrow10UpView: ImageView
    private lateinit var arrow10DownView: ImageView
    private lateinit var arrow10UpRenderable: Renderable
    private lateinit var arrow10DownRenderable: Renderable

    private var cubeRenderable: ModelRenderable? = null
    private var distanceCardViewRenderable: ViewRenderable? = null


    private val placedAnchors = ArrayList<Anchor>()
    private val placedAnchorNodes = ArrayList<AnchorNode>()
    private val midAnchors: MutableMap<String, Anchor> = mutableMapOf()
    private val midAnchorNodes: MutableMap<String, AnchorNode> = mutableMapOf()
    private val fromGroundNodes = ArrayList<List<Node>>()

    private lateinit var initCM: String

    private lateinit var clearButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkIsSupportedDeviceOrFinish(this)) {
            Toast.makeText(applicationContext, "Device not supported", Toast.LENGTH_LONG)
                .show()
        }

        setContentView(R.layout.activity_medicion)

        arFragment = supportFragmentManager.findFragmentById(R.id.sceneform_fragment) as ArFragment?
        distanceModeTextView = findViewById(R.id.distance_view)

        initCM = resources.getString(R.string.initCM)

        initArrowView()
        initRenderable()
        clearButton()

        arFragment!!.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane?, motionEvent: MotionEvent? ->
            if (cubeRenderable == null || distanceCardViewRenderable == null) return@setOnTapArPlaneListener
            // Creating Anchor.
            tapDistanceOf2Points(hitResult)
        }

    }

    private fun tapDistanceOf2Points(hitResult: HitResult){
        if (placedAnchorNodes.size == 0){
            placeAnchor(hitResult, cubeRenderable!!)
        }
        else if (placedAnchorNodes.size == 1){
            placeAnchor(hitResult, cubeRenderable!!)

            val midPosition = floatArrayOf(
                (placedAnchorNodes[0].worldPosition.x + placedAnchorNodes[1].worldPosition.x) / 2,
                (placedAnchorNodes[0].worldPosition.y + placedAnchorNodes[1].worldPosition.y) / 2,
                (placedAnchorNodes[0].worldPosition.z + placedAnchorNodes[1].worldPosition.z) / 2)
            val quaternion = floatArrayOf(0.0f,0.0f,0.0f,0.0f)
            val pose = Pose(midPosition, quaternion)

            placeMidAnchor(pose, distanceCardViewRenderable!!)
        }
        else {
            clearAllAnchors()
            placeAnchor(hitResult, cubeRenderable!!)
        }
    }

    private fun placeMidAnchor(pose: Pose,
                               renderable: Renderable,
                               between: Array<Int> = arrayOf(0,1)){
        val midKey = "${between[0]}_${between[1]}"
        val anchor = arFragment!!.arSceneView.session!!.createAnchor(pose)
        midAnchors.put(midKey, anchor)

        val anchorNode = AnchorNode(anchor).apply {
            isSmoothed = true
            setParent(arFragment!!.arSceneView.scene)
        }
        midAnchorNodes.put(midKey, anchorNode)

        val node = TransformableNode(arFragment!!.transformationSystem)
            .apply{
                this.rotationController.isEnabled = false
                this.scaleController.isEnabled = false
                this.translationController.isEnabled = true
                this.renderable = renderable
                setParent(anchorNode)
            }
        arFragment!!.arSceneView.scene.addOnUpdateListener(this)
        arFragment!!.arSceneView.scene.addChild(anchorNode)
    }

    private fun placeAnchor(hitResult: HitResult,
                            renderable: Renderable){
        val anchor = hitResult.createAnchor()
        placedAnchors.add(anchor)

        val anchorNode = AnchorNode(anchor).apply {
            isSmoothed = true
            setParent(arFragment!!.arSceneView.scene)
        }
        placedAnchorNodes.add(anchorNode)

        val node = TransformableNode(arFragment!!.transformationSystem)
            .apply{
                this.rotationController.isEnabled = false
                this.scaleController.isEnabled = false
                this.translationController.isEnabled = true
                this.renderable = renderable
                setParent(anchorNode)
            }

        arFragment!!.arSceneView.scene.addOnUpdateListener(this)
        arFragment!!.arSceneView.scene.addChild(anchorNode)
        node.select()
    }

    private fun clearButton(){
        clearButton = findViewById(R.id.clearButton)
        clearButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                clearAllAnchors()
            }
        })
    }

    private fun clearAllAnchors(){
        placedAnchors.clear()
        for (anchorNode in placedAnchorNodes){
            arFragment!!.arSceneView.scene.removeChild(anchorNode)
            anchorNode.isEnabled = false
            anchorNode.anchor!!.detach()
            anchorNode.setParent(null)
        }
        placedAnchorNodes.clear()
        midAnchors.clear()
        for ((k,anchorNode) in midAnchorNodes){
            arFragment!!.arSceneView.scene.removeChild(anchorNode)
            anchorNode.isEnabled = false
            anchorNode.anchor!!.detach()
            anchorNode.setParent(null)
        }
        midAnchorNodes.clear()
        fromGroundNodes.clear()
    }

    private fun initRenderable() {
        MaterialFactory.makeTransparentWithColor(
            this,
            arColor(Color.RED))
            .thenAccept { material: Material? ->
                cubeRenderable = ShapeFactory.makeSphere(
                    0.02f,
                    Vector3.zero(),
                    material)
                cubeRenderable!!.setShadowCaster(false)
                cubeRenderable!!.setShadowReceiver(false)
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }

        ViewRenderable
            .builder()
            .setView(this, R.layout.distancia_text_layout)
            .build()
            .thenAccept{
                distanceCardViewRenderable = it
                distanceCardViewRenderable!!.isShadowCaster = false
                distanceCardViewRenderable!!.isShadowReceiver = false
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }

        ViewRenderable
            .builder()
            .setView(this, arrow1UpLinearLayout)
            .build()
            .thenAccept{
                arrow1UpRenderable = it
                arrow1UpRenderable.isShadowCaster = false
                arrow1UpRenderable.isShadowReceiver = false
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }

        ViewRenderable
            .builder()
            .setView(this, arrow1DownLinearLayout)
            .build()
            .thenAccept{
                arrow1DownRenderable = it
                arrow1DownRenderable.isShadowCaster = false
                arrow1DownRenderable.isShadowReceiver = false
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }

        ViewRenderable
            .builder()
            .setView(this, arrow10UpLinearLayout)
            .build()
            .thenAccept{
                arrow10UpRenderable = it
                arrow10UpRenderable.isShadowCaster = false
                arrow10UpRenderable.isShadowReceiver = false
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }

        ViewRenderable
            .builder()
            .setView(this, arrow10DownLinearLayout)
            .build()
            .thenAccept{
                arrow10DownRenderable = it
                arrow10DownRenderable.isShadowCaster = false
                arrow10DownRenderable.isShadowReceiver = false
            }
            .exceptionally {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(it.message).setTitle("Error")
                val dialog = builder.create()
                dialog.show()
                return@exceptionally null
            }
    }



    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        val openGlVersionString =
            (Objects.requireNonNull(activity
                .getSystemService(Context.ACTIVITY_SERVICE)) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES ${MIN_OPENGL_VERSION} later")
            Toast.makeText(activity,
                "Sceneform requires OpenGL ES ${MIN_OPENGL_VERSION} or later",
                Toast.LENGTH_LONG)
                .show()
            activity.finish()
            return false
        }
        return true
    }

    private fun measureDistanceOf2Points(){
        if (placedAnchorNodes.size == 2) {
            val distanceMeter = calculateDistance(
                placedAnchorNodes[0].worldPosition,
                placedAnchorNodes[1].worldPosition)
            measureDistanceOf2Points(distanceMeter)
        }
    }

    private fun measureDistanceOf2Points(distanceMeter: Float){
        val distanceTextCM = makeDistanceTextWithCM(distanceMeter)
        val textView = findViewById<TextView>(R.id.distance_view)
        textView.text = distanceTextCM
        Log.d(TAG, "distance: ${distanceTextCM}")
    }

    private fun makeDistanceTextWithCM(distanceMeter: Float): String{
        val distanceCM = changeUnit(distanceMeter, "cm")
        val distanceCMFloor = "%.2f".format(distanceCM)
        return "${distanceCMFloor} cm"
    }

    private fun changeUnit(distanceMeter: Float, unit: String): Float{
        return when(unit){
            "cm" -> distanceMeter * 100
            "mm" -> distanceMeter * 1000
            else -> distanceMeter
        }
    }

    private fun calculateDistance(x: Float, y: Float, z: Float): Float{
        return sqrt(x.pow(2) + y.pow(2) + z.pow(2))
    }

    private fun calculateDistance(objectPose0: Pose, objectPose1: Pose): Float{
        return calculateDistance(
            objectPose0.tx() - objectPose1.tx(),
            objectPose0.ty() - objectPose1.ty(),
            objectPose0.tz() - objectPose1.tz())
    }


    private fun calculateDistance(objectPose0: Vector3, objectPose1: Pose): Float{
        return calculateDistance(
            objectPose0.x - objectPose1.tx(),
            objectPose0.y - objectPose1.ty(),
            objectPose0.z - objectPose1.tz()
        )
    }

    private fun calculateDistance(objectPose0: Vector3, objectPose1: Vector3): Float{
        return calculateDistance(
            objectPose0.x - objectPose1.x,
            objectPose0.y - objectPose1.y,
            objectPose0.z - objectPose1.z
        )
    }

    private fun initArrowView(){
        arrow1UpLinearLayout = LinearLayout(this)
        arrow1UpLinearLayout.orientation = LinearLayout.VERTICAL
        arrow1UpLinearLayout.gravity = Gravity.CENTER
        arrow1UpView = ImageView(this)
        arrow1UpView.setImageResource(R.drawable.arrow_1up)
        arrow1UpLinearLayout.addView(arrow1UpView,
                Constantes.arrowViewSize,
                Constantes.arrowViewSize)

        arrow1DownLinearLayout = LinearLayout(this)
        arrow1DownLinearLayout.orientation = LinearLayout.VERTICAL
        arrow1DownLinearLayout.gravity = Gravity.CENTER
        arrow1DownView = ImageView(this)
        arrow1DownView.setImageResource(R.drawable.arrow_1down)
        arrow1DownLinearLayout.addView(arrow1DownView,
                Constantes.arrowViewSize,
                Constantes.arrowViewSize)

        arrow10UpLinearLayout = LinearLayout(this)
        arrow10UpLinearLayout.orientation = LinearLayout.VERTICAL
        arrow10UpLinearLayout.gravity = Gravity.CENTER
        arrow10UpView = ImageView(this)
        arrow10UpView.setImageResource(R.drawable.arrow_10up)
        arrow10UpLinearLayout.addView(arrow10UpView,
                Constantes.arrowViewSize,
                Constantes.arrowViewSize)

        arrow10DownLinearLayout = LinearLayout(this)
        arrow10DownLinearLayout.orientation = LinearLayout.VERTICAL
        arrow10DownLinearLayout.gravity = Gravity.CENTER
        arrow10DownView = ImageView(this)
        arrow10DownView.setImageResource(R.drawable.arrow_10down)
        arrow10DownLinearLayout.addView(arrow10DownView,
                Constantes.arrowViewSize,
                Constantes.arrowViewSize)
    }

    @SuppressLint("SetTextI18n")
    override fun onUpdate(frameTime: FrameTime) {
        measureDistanceOf2Points()
    }
}