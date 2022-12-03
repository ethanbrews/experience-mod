package me.ethanbrews.experience.error

/** An exception thrown when data is loaded that follows an invalid format */
class InvalidFormatException : Exception {
    constructor() : super()
    constructor(msg: String) : super(msg)
    constructor(throwable: Throwable) : super(throwable)
    constructor(msg: String, throwable: Throwable) : super(msg, throwable)
}