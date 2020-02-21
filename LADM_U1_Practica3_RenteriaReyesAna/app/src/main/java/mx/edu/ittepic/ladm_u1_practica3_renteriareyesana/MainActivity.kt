package mx.edu.ittepic.ladm_u1_practica3_renteriareyesana

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {
        //DECLARACION DE VARIABLES
        var vectorEstatico : Array<Int> = Array(10,{ 0 })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

         ///////////////// ASIGNAR LOS DATOS AL VECTOR ////////////////////777
        btnAsignar.setOnClickListener {
            if(editValor.text.toString().isEmpty() || editPosicion.text.toString().isEmpty()){
                AlertDialog.Builder(this).setTitle("ATENCION").setMessage("ERROR HAY UN CAMPO VACIO").show()
                return@setOnClickListener
            }
            if(editPosicion.text.toString().toInt() > 9){
                AlertDialog.Builder(this).setTitle("ATENCION").setMessage("ERROR SOLO PUEDES PONER UN NUMERO DEL 0 AL 9").show()
                return@setOnClickListener
            }

            var posicion = editPosicion.text.toString().toInt()
            var valor = editValor.text.toString().toInt()

           vectorEstatico[posicion] = valor
            editValor.setText("")
            editPosicion.setText("")

        }
         ////////////////////////// MOSTRAR LOS DATOS EN ETIQUETA ///////////////////7
        btnMostrar.setOnClickListener {
            var data = ""

            (0..9).forEach {
                data += "${vectorEstatico[it]},"
            }
            textMostrar.setText(data)
        }

        ////////////////////////     GUARDAR DATOS EN MEMORIA SD //////////////////////////
        btnGuardar.setOnClickListener {
            //recuperar el nombre del archivo
            if (noSD()){
                mensaje("NO HAY UNA MEMORIA SD")
                return@setOnClickListener
            }
            if(editGuardar.text.toString().isEmpty()){
                AlertDialog.Builder(this).setTitle("ATENCION").setMessage("ERROR EL CAMPO DEL NOMBRE DEL ARCHIVO ESTA VACIO").show()
                return@setOnClickListener
            }
            try {
                if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                    //PERMISO NO CONCEDIDO, ENTONCES SOLICITAR EL PERMISO
                    ActivityCompat.requestPermissions(this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE),0)  //solicita el permiso, REQUESTCODE = 0 PARA DAR POR ENTENDIDO QUE SI SE PUDO REALIZAR LA ACCION
                }
                var rutaSD = Environment.getExternalStorageDirectory()
                var datosArchivo = File(rutaSD.absolutePath,editGuardar.text.toString()+".txt") //file (ruta,nombre)
                var flujoSalida = OutputStreamWriter( FileOutputStream(datosArchivo))
                //Guardar los datos de la etiqueta en la variable data
                var data = textMostrar.text.toString()

                flujoSalida.write(data)
                flujoSalida.flush() //forzar escritura
                flujoSalida.close()
                mensaje("El archivo se ha creado correctamente en la memoria SD")
                editGuardar.setText("")
            }catch (error: IOException){
                mensaje(error.message.toString())
            }
        }

        ///////////////////////// LEER DATOS DE MEMORIA SD ////////////////////////////////
        btnLeerMemoria.setOnClickListener{
            if(noSD()){
                mensaje("NO HAY UNA MEMORIA SD")
                return@setOnClickListener
            }
            if(editLeer.text.toString().isEmpty()){
                AlertDialog.Builder(this).setTitle("ATENCION").setMessage("ERROR EL CAMPO DE LEER ARCHIVO ESTA VACIO").show()
                return@setOnClickListener
            }
            try {
                var rutaSD = Environment.getExternalStorageDirectory()
                var datosArchivo = File(rutaSD.absolutePath,editLeer.text.toString()+".txt") //file (ruta,nombre)
                var flujoEntrada = BufferedReader(InputStreamReader(FileInputStream(datosArchivo))) //BufferedReader = leer por linea -- esto es acceso a la memoria interna

                var data = flujoEntrada.readLine()

                textMostrar.setText(data)

                var vector = data.split(",")

                (0..9).forEach {
                    vectorEstatico[it] = vector[it].toInt()
                }
                editLeer.setText("")

            }catch (error : IOException){
                mensaje(error.message.toString())
            }

        }

    }
    /////////////////////// METODOS AUXILIARES /////////////////////
    fun noSD():Boolean{
        var estado = Environment.getExternalStorageState() //regresa el estado de si hay una SD Montada
        if(estado != Environment.MEDIA_MOUNTED) //regresa si esta montada una SD
        {
            return true
        }
        return false
    }

    fun mensaje(m:String){
        AlertDialog.Builder(this)
            .setTitle("ATENCION")
            .setMessage(m)
            .setPositiveButton("ACEPTAR"){d, i->}
            .show()
    }


}
