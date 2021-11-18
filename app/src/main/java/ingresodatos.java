import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ort.tp5dai.R;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ingresodatos {
    EditText nombreCiudadIngresada;
    Button buscar;

    String estadoDeClimaActual;
    String nomCity;
    JsonObject objetoMain;
    JsonObject objetoWind;
    String temperaturaCiudad;
    String sensaciontermicaCiudad;
    String humedadCiudad;
    String velocidadViento;
    public View onCreateView(LayoutInflater infladorDeLayout, ViewGroup GrupoDeLaVista, Bundle datosRecibidos){
        View VistaADevolver;

        VistaADevolver=infladorDeLayout.inflate(R.layout.ingreso, GrupoDeLaVista, false);

        nombreCiudadIngresada=VistaADevolver.findViewById(R.id.nombreCiudad);
        buscar=VistaADevolver.findViewById(R.id.buscarCiudad);

        buscar.setOnClickListener((View.OnClickListener) this);

        return VistaADevolver;
    }
    public void onClick(View vistaRecibida){
        //String nombreCiudadIngresado;

        tareaAsincronica miTarea=new tareaAsincronica();
        miTarea.execute();

        Log.d("Acceso API", "Ejecute tarea asincronica");

        //nombreCiudadIngresado=nombreCiudadIngresada.getText().toString();

    }

    private class tareaAsincronica extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids){
            try{
                String nombre = nombreCiudadIngresada.getText().toString();
                URL miRuta;
                miRuta=new URL("https://api.openweathermap.org/data/2.5/weather?q="+nombre+"&appid=2f852d2d335870a58c1c6fdf3db39907&units=metric&lang=es");
                HttpURLConnection miConexion=(HttpURLConnection) miRuta.openConnection();

                Log.d("Acceso APi", "Conexion OK");
                if(miConexion.getResponseCode()==200){
                    InputStream cuerpoRespuesta=miConexion.getInputStream();
                    InputStreamReader lectoRespuesta=new InputStreamReader(cuerpoRespuesta, "UTF-8");

                    Log.d("Acceso APi", "Me conecte ok");

                    procesarJson(lectoRespuesta);
                }
                else {
                    Log.d("Acceso API", "Error en la conexion");
                }
                miConexion.disconnect();
            } catch(Exception Error){
                Log.d("Acceso API","Hubo un error al conectarme "+Error.getMessage());

            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid){
            super.onPostExecute(aVoid);
            MainActivity miActivityAnfitriona;
            miActivityAnfitriona=(MainActivity)getActivity();
            miActivityAnfitriona.RecibirDatosIngresados(nomCity, estadoDeClimaActual, temperaturaCiudad, sensaciontermicaCiudad, humedadCiudad, velocidadViento);

            //milista.setAdapter(adaptador);
        }
    }

    private void procesarJson(InputStreamReader lectoRespuesta) {
        JsonParser parseadorDeJson;
        parseadorDeJson = new JsonParser();

        JsonObject objetoJsonCrudo;
        objetoJsonCrudo = parseadorDeJson.parse(lectoRespuesta).getAsJsonObject();

        try {

            String nombre;
            nombre = objetoJsonCrudo.get("name").getAsString();
            nomCity = nombre;

            Log.d("Acceso API", "Nombre de la ciudad:"+nomCity);

            JsonArray arrCiudades;
            arrCiudades = objetoJsonCrudo.get("weather").getAsJsonArray();

            for (int posicion = 0; posicion < arrCiudades.size(); posicion++) {
                JsonObject objetoUnaPelicula;
                objetoUnaPelicula = arrCiudades.get(posicion).getAsJsonObject();

                Log.d("Acceso API", "Estoy procesanso el array");

                String estadoDeTemperatura;
                estadoDeTemperatura = objetoUnaPelicula.get("main").getAsString();
                estadoDeClimaActual = estadoDeTemperatura;

                Log.d("Acceso API", "Estado de clima actual:"+estadoDeClimaActual);

                Log.d("Acceso API", "Termine de recorrer el for");
            }

            JsonObject objMain;
            objMain = objetoJsonCrudo.get("main").getAsJsonObject();
            objetoMain = objMain;
            Log.d("Acceso API", "Objeto main:"+objetoMain);

            String temperatura;
            temperatura = objMain.get("temp").getAsString();
            temperaturaCiudad = temperatura;
            Log.d("Acceso API", "Temperatura:"+temperaturaCiudad);

            String sensacionTermica;
            sensacionTermica = objMain.get("feels_like").getAsString();
            sensaciontermicaCiudad = sensacionTermica;
            Log.d("Acceso API", "Sensacion termica:"+sensaciontermicaCiudad);

            String humedad;
            humedad = objMain.get("humidity").getAsString();
            humedadCiudad = humedad;
            Log.d("Acceso API", "Humedad:"+humedadCiudad);

            JsonObject objwind;
            objwind = objetoJsonCrudo.get("wind").getAsJsonObject();
            objetoWind = objwind;
            Log.d("Acceso API", "Objeto wind:"+objetoWind);

            String veloWind;
            veloWind = objwind.get("speed").getAsString();
            velocidadViento = veloWind;
            Log.d("Acceso API", "Velocidad viento:"+velocidadViento);

        } catch (Exception Error) {
            Log.d("Acceso API", "Hubo un error al procesar el json " + Error.getMessage());

        }
    }
}
