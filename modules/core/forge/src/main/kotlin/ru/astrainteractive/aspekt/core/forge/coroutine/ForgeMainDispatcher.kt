package ru.astrainteractive.aspekt.core.forge.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.loading.FMLEnvironment
import kotlin.coroutines.CoroutineContext

// object ForgeMainDispatcher : MainCoroutineDispatcher() {
//    private val dispatcher by lazy {
//        when (FMLEnvironment.dist) {
//            Dist.CLIENT -> LogicalSidedProvider.WORKQUEUE.get(LogicalSide.CLIENT)
//            Dist.DEDICATED_SERVER -> LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER)
//        }.asCoroutineDispatcher()
//    }
//
//    override val immediate: MainCoroutineDispatcher get() = this
//
//    override fun dispatch(context: CoroutineContext, block: Runnable) {
//        dispatcher.dispatch(context, block)
//    }
// }
object ForgeMainDispatcher : CoroutineDispatcher() {

    override fun isDispatchNeeded(context: CoroutineContext): Boolean {
        return true
    }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        DistExecutor.safeRunWhenOn(FMLEnvironment.dist) {
            DistExecutor.SafeRunnable { block.run() }
        }
    }
}
