package os.manager

import os.desktop.SubWindowData

object ProgramsManager {
    private val programsThatOpen = mutableListOf<SubWindowData>()
    private val onOpen = mutableListOf<(data: SubWindowData) -> Unit>()
    private val onClose = mutableListOf<(data: SubWindowData) -> Unit>()

    fun open(data: SubWindowData) {
        programsThatOpen.add(data)

        onOpen.forEach {
            it(data)
        }
    }

    fun close(data: SubWindowData) {
        programsThatOpen.remove(data)

        onClose.forEach {
            it(data)
        }
    }

    fun openAll(data: Collection<SubWindowData>) {
        this.programsThatOpen.addAll(data)

        data.forEach { fData ->
            onOpen.forEach {
                it(fData)
            }
        }
    }

    fun closeAll(data: Collection<SubWindowData>) {
        this.programsThatOpen.removeAll(data)

        data.forEach { fData ->
            onClose.forEach {
                it(fData)
            }
        }
    }

    fun count(): Int {
        return programsThatOpen.size
    }

    fun forEach(lambda: (data: SubWindowData) -> Unit) {
        programsThatOpen.forEach {
            lambda(it)
        }
    }

    fun getAll(): List<SubWindowData> {
        return programsThatOpen.toList()
    }

    fun addOnSent(onOpen: (data: SubWindowData) -> Unit) {
        this.onOpen.add(onOpen)
    }

    fun removeOnSent(onOpen: (data: SubWindowData) -> Unit) {
        this.onOpen.remove(onOpen)
    }

    fun addOnClose(onClose: (data: SubWindowData) -> Unit) {
        this.onClose.add(onClose)
    }

    fun removeOnClose(onClose: (data: SubWindowData) -> Unit) {
        this.onClose.remove(onClose)
    }
}