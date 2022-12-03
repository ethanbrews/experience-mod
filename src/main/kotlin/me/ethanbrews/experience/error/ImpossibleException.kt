package me.ethanbrews.experience.error

/** An exception when an instruction executes that was thought to be impossible */
class ImpossibleException : Exception {
    constructor() : super()
    constructor(msg: String) : super(msg)
    constructor(throwable: Throwable) : super(throwable)
    constructor(msg: String, throwable: Throwable) : super(msg, throwable)
}