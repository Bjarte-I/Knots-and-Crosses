package com.example.knotsandcrosses.api

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.knotsandcrosses.App
import com.example.knotsandcrosses.R
import com.example.knotsandcrosses.api.data.Game
import com.example.knotsandcrosses.api.data.GameState
import com.google.gson.Gson
import org.json.JSONObject


typealias GameServiceCallback = (state: Game?, errorCode:Int? ) -> Unit

/*  NOTE:
    Using object expression to make GameService a Singleton.
    Why? Because there should only be one active GameService ever.
 */

object GameService {

    /// NOTE: Do not want to have App.context all over the code. Also it is nice if we later want to support different contexts
    private val context = App.context

    /// NOTE: Good practice to use a queue for performing requests.
    private val requestQueue: RequestQueue = Volley.newRequestQueue(context)

    /// NOTE: One possible way of constructing a list of API url. You want to construct the urls so that you can support different environments (i.e. Debug, Test, Prod etc)
    private enum class APIEndpoints(val url:String) {
        CREATE_GAME("%1s%2s%3s".format(context.getString(R.string.protocol), context.getString(R.string.domain),context.getString(R.string.base_path))),
        JOIN_GAME("%1s%2s%3s".format(context.getString(R.string.protocol), context.getString(R.string.domain), context.getString(R.string.join_game_path))),
        POLL_GAME("%1s%2s%3s".format(context.getString(R.string.protocol), context.getString(R.string.domain), context.getString(R.string.poll_game_path))),
        UPDATE_GAME("%1s%2s%3s".format(context.getString(R.string.protocol), context.getString(R.string.domain), context.getString(R.string.update_game_path)))
    }


    fun createGame(playerId:String, state:GameState, callback:GameServiceCallback) {

        val url = APIEndpoints.CREATE_GAME.url

        val requestData = JSONObject()
        requestData.put("player", playerId)
        requestData.put("state", state)

        val request = object : JsonObjectRequest(
            Request.Method.POST,url, requestData,
            {
                // Success game created.
                val game = Gson().fromJson(it.toString(0), Game::class.java)
                callback(game,null)
            }, {
                // Error creating new game.
                callback(null, it.networkResponse.statusCode)
            } ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Game-Service-Key"] = context.getString(R.string.game_service_key)
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun joinGame(playerId:String, gameId:String, callback: GameServiceCallback){
        val url = APIEndpoints.JOIN_GAME.url

        val requestData = JSONObject()
        requestData.put("player", playerId)
        requestData.put("gameId", gameId)

        val request = object : JsonObjectRequest(
            Method.POST,url, requestData,
            {
                // Success game joined.
                val game = Gson().fromJson(it.toString(0), Game::class.java)
                callback(game,null)
            }, {
                // Error joining game.
                callback(null, it.networkResponse.statusCode)
            } ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Game-Service-Key"] = context.getString(R.string.game_service_key)
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun updateGame(gameId: String, gameState:GameState, callback: GameServiceCallback){
        val url = APIEndpoints.UPDATE_GAME.url

        val requestData = JSONObject()
        requestData.put("gameId", gameId)
        requestData.put("gameState", gameState)

        val request = object : JsonObjectRequest(
            Method.POST,url, requestData,
            {
                // Success game updated.
                val game = Gson().fromJson(it.toString(0), Game::class.java)
                callback(game,null)
            }, {
                // Error updating game.
                callback(null, it.networkResponse.statusCode)
            } ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Game-Service-Key"] = context.getString(R.string.game_service_key)
                return headers
            }
        }

        requestQueue.add(request)
    }

    fun pollGame(gameId: String,callback:GameServiceCallback){
        val url = APIEndpoints.POLL_GAME.url

        val requestData = JSONObject()
        requestData.put("gameId", gameId)

        val request = object: JsonObjectRequest(
            Method.GET, url, requestData,
            {
                val game = Gson().fromJson(it.toString(0), Game::class.java)
                callback(game, null)
            }, {
                callback(null, it.networkResponse.statusCode)
            } ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                headers["Game-Service-Key"] = context.getString(R.string.game_service_key)
                return headers
            }
        }

        requestQueue.add(request)
    }

}