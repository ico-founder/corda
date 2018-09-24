@file:Suppress("unused")

package net.corda.docs.kotlin.tutorial.twoparty

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.flows.FlowLogic
import net.corda.core.flows.FlowSession
import net.corda.core.flows.InitiatedBy
import net.corda.docs.kotlin.tutorial.helloworld.IOUFlow
import net.corda.docs.kotlin.tutorial.helloworld.IOUState

// DOCSTART 01
// Add these imports:
import net.corda.core.contracts.requireThat
import net.corda.core.flows.*
import net.corda.core.transactions.SignedTransaction

// Define IOUFlowResponder:
@InitiatedBy(IOUFlow::class)
class IOUFlowResponder(val otherPartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        subFlow(object : SignAndWaitForCommitFlow(otherPartySession) {
            override fun checkTxBeforeSigning(stx: SignedTransaction) {
                requireThat {
                    val output = stx.tx.outputs.single().data
                    "This must be an IOU transaction." using (output is IOUState)
                    val iou = output as IOUState
                    "The IOU's value can't be too high." using (iou.value < 100)
                }
            }
        })
    }
}
// DOCEND 01