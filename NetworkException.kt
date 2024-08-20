package com.example.ezemkofi.Tools

class NetworkException() {
    class IgnorableException(msg: String) : Exception(msg)
    class AuthException : Exception("You are not authorized")
}