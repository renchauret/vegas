package extensions

private fun <T> List<T>.findDuplicates(): List<T> {
    val duplicates = mutableListOf<T>()
    val seenOnce = mutableListOf<T>()
    this.forEach {
        if (seenOnce.contains(it)) {
            duplicates += it
            seenOnce.remove(it)
        } else {
            seenOnce += it
        }
    }
    return duplicates
}

fun <T> List<T>.filterDuplicates(): List<T> {
    val duplicates = this.findDuplicates()
    return this.filter { !duplicates.contains(it) }
}
