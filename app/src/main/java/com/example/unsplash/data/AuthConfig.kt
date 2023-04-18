package com.example.unsplash.data

import net.openid.appauth.ResponseTypeValues

object AuthConfig {
    const val AUTH_URI = "https://unsplash.com/oauth/authorize"
    const val TOKEN_URI = "https://unsplash.com/oauth/token"
    const val RESPONSE_TYPE = ResponseTypeValues.CODE
    const val SCOPE = "public read_user read_photos write_likes read_collections write_collections"
    const val CLIENT_ID = "Hn5OzVTsvQQcUkaS7QxDG7rwTSWXlq-FyADjy7QdS5U"
    const val CLIENT_SECRET = "eyXeY_Y1lEqm10gcKHpgj1kc6wajVNhnXq43Uvtu7mU"
    const val REDIRECT_URL = "skillbox://skillbox.ru/callback"
}
