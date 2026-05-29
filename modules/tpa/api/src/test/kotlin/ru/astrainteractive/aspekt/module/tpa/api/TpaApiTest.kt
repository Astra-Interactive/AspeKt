package ru.astrainteractive.aspekt.module.tpa.api

import ru.astrainteractive.aspekt.module.tpa.model.TpaApiRequestType
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TpaApiTest {

    private fun tpaApi() = TpaApi()

    // hasPendingRequest

    @Test
    fun GIVEN_no_requests_WHEN_has_pending_request_THEN_returns_false() {
        val api = tpaApi()
        assertFalse(api.hasPendingRequest(UUID.randomUUID()))
    }

    @Test
    fun GIVEN_tpa_requested_WHEN_has_pending_request_on_executor_THEN_returns_true() {
        val api = tpaApi()
        val executor = UUID.randomUUID()
        api.tpa(executor, UUID.randomUUID())
        assertTrue(api.hasPendingRequest(executor))
    }

    @Test
    fun GIVEN_tpa_here_requested_WHEN_has_pending_request_on_executor_THEN_returns_true() {
        val api = tpaApi()
        val executor = UUID.randomUUID()
        api.tpaHere(executor, UUID.randomUUID())
        assertTrue(api.hasPendingRequest(executor))
    }

    @Test
    fun GIVEN_tpa_requested_WHEN_has_pending_request_on_target_THEN_returns_false() {
        val api = tpaApi()
        val target = UUID.randomUUID()
        api.tpa(UUID.randomUUID(), target)
        assertFalse(api.hasPendingRequest(target))
    }

    // isBeingWaited

    @Test
    fun GIVEN_no_requests_WHEN_is_being_waited_THEN_returns_false() {
        val api = tpaApi()
        assertFalse(api.isBeingWaited(UUID.randomUUID()))
    }

    @Test
    fun GIVEN_tpa_requested_WHEN_is_being_waited_on_target_THEN_returns_true() {
        val api = tpaApi()
        val target = UUID.randomUUID()
        api.tpa(UUID.randomUUID(), target)
        assertTrue(api.isBeingWaited(target))
    }

    @Test
    fun GIVEN_tpa_requested_WHEN_is_being_waited_on_executor_THEN_returns_false() {
        val api = tpaApi()
        val executor = UUID.randomUUID()
        api.tpa(executor, UUID.randomUUID())
        assertFalse(api.isBeingWaited(executor))
    }

    @Test
    fun GIVEN_tpa_here_requested_WHEN_is_being_waited_on_target_THEN_returns_true() {
        val api = tpaApi()
        val target = UUID.randomUUID()
        api.tpaHere(UUID.randomUUID(), target)
        assertTrue(api.isBeingWaited(target))
    }

    // get

    @Test
    fun GIVEN_no_requests_WHEN_get_THEN_returns_empty_map() {
        val api = tpaApi()
        assertTrue(api.get(UUID.randomUUID()).isEmpty())
    }

    @Test
    fun GIVEN_tpa_requested_WHEN_get_for_target_THEN_returns_request_with_tpa_type() {
        val api = tpaApi()
        val executor = UUID.randomUUID()
        val target = UUID.randomUUID()
        api.tpa(executor, target)
        val result = api.get(target)
        assertEquals(1, result.size)
        assertEquals(TpaApiRequestType.TPA, result[executor]?.type)
        assertEquals(target, result[executor]?.targetUuid)
    }

    @Test
    fun GIVEN_tpa_here_requested_WHEN_get_for_target_THEN_returns_request_with_tpa_here_type() {
        val api = tpaApi()
        val executor = UUID.randomUUID()
        val target = UUID.randomUUID()
        api.tpaHere(executor, target)
        val result = api.get(target)
        assertEquals(1, result.size)
        assertEquals(TpaApiRequestType.TPAHERE, result[executor]?.type)
    }

    @Test
    fun GIVEN_tpa_requested_WHEN_get_for_executor_THEN_returns_empty_map() {
        val api = tpaApi()
        val executor = UUID.randomUUID()
        api.tpa(executor, UUID.randomUUID())
        assertTrue(api.get(executor).isEmpty())
    }

    @Test
    fun GIVEN_multiple_requestors_to_same_target_WHEN_get_THEN_returns_all_requests() {
        val api = tpaApi()
        val executorA = UUID.randomUUID()
        val executorB = UUID.randomUUID()
        val target = UUID.randomUUID()
        api.tpa(executorA, target)
        api.tpa(executorB, target)
        val result = api.get(target)
        assertEquals(2, result.size)
        assertTrue(executorA in result)
        assertTrue(executorB in result)
    }

    // cancel

    @Test
    fun GIVEN_pending_request_WHEN_cancel_THEN_has_pending_request_returns_false() {
        val api = tpaApi()
        val executor = UUID.randomUUID()
        api.tpa(executor, UUID.randomUUID())
        api.cancel(executor)
        assertFalse(api.hasPendingRequest(executor))
    }

    @Test
    fun GIVEN_pending_request_WHEN_cancel_THEN_target_is_no_longer_being_waited() {
        val api = tpaApi()
        val executor = UUID.randomUUID()
        val target = UUID.randomUUID()
        api.tpa(executor, target)
        api.cancel(executor)
        assertFalse(api.isBeingWaited(target))
    }

    @Test
    fun GIVEN_no_pending_request_WHEN_cancel_THEN_no_effect() {
        val api = tpaApi()
        val player = UUID.randomUUID()
        api.cancel(player)
        assertFalse(api.hasPendingRequest(player))
    }

    // deny

    @Test
    fun GIVEN_no_requests_for_target_WHEN_deny_THEN_returns_empty_set() {
        val api = tpaApi()
        assertTrue(api.deny(UUID.randomUUID()).isEmpty())
    }

    @Test
    fun GIVEN_single_requestor_WHEN_deny_THEN_returns_requestor_uuid() {
        val api = tpaApi()
        val executor = UUID.randomUUID()
        val target = UUID.randomUUID()
        api.tpa(executor, target)
        assertEquals(setOf(executor), api.deny(target))
    }

    @Test
    fun GIVEN_single_requestor_WHEN_deny_THEN_requestor_has_no_pending_request() {
        val api = tpaApi()
        val executor = UUID.randomUUID()
        val target = UUID.randomUUID()
        api.tpa(executor, target)
        api.deny(target)
        assertFalse(api.hasPendingRequest(executor))
    }

    @Test
    fun GIVEN_multiple_requestors_WHEN_deny_THEN_all_requestors_returned() {
        val api = tpaApi()
        val executorA = UUID.randomUUID()
        val executorB = UUID.randomUUID()
        val target = UUID.randomUUID()
        api.tpa(executorA, target)
        api.tpaHere(executorB, target)
        assertEquals(setOf(executorA, executorB), api.deny(target))
    }

    @Test
    fun GIVEN_multiple_requestors_WHEN_deny_THEN_all_requests_removed() {
        val api = tpaApi()
        val executorA = UUID.randomUUID()
        val executorB = UUID.randomUUID()
        val target = UUID.randomUUID()
        api.tpa(executorA, target)
        api.tpa(executorB, target)
        api.deny(target)
        assertFalse(api.hasPendingRequest(executorA))
        assertFalse(api.hasPendingRequest(executorB))
        assertFalse(api.isBeingWaited(target))
    }

    @Test
    fun GIVEN_unrelated_request_exists_WHEN_deny_other_target_THEN_unrelated_request_unaffected() {
        val api = tpaApi()
        val executorA = UUID.randomUUID()
        val targetA = UUID.randomUUID()
        val executorB = UUID.randomUUID()
        val targetB = UUID.randomUUID()
        api.tpa(executorA, targetA)
        api.tpa(executorB, targetB)
        api.deny(targetA)
        assertTrue(api.hasPendingRequest(executorB))
        assertTrue(api.isBeingWaited(targetB))
    }

    // overwrite

    @Test
    fun GIVEN_existing_tpa_request_WHEN_tpa_called_again_with_new_target_THEN_old_target_no_longer_waited() {
        val api = tpaApi()
        val executor = UUID.randomUUID()
        val targetFirst = UUID.randomUUID()
        val targetSecond = UUID.randomUUID()
        api.tpa(executor, targetFirst)
        api.tpa(executor, targetSecond)
        assertFalse(api.isBeingWaited(targetFirst))
        assertTrue(api.isBeingWaited(targetSecond))
        assertEquals(targetSecond, api.get(targetSecond)[executor]?.targetUuid)
    }

    @Test
    fun GIVEN_existing_tpa_request_WHEN_tpa_here_called_by_same_executor_THEN_type_overwritten() {
        val api = tpaApi()
        val executor = UUID.randomUUID()
        val target = UUID.randomUUID()
        api.tpa(executor, target)
        api.tpaHere(executor, target)
        assertEquals(TpaApiRequestType.TPAHERE, api.get(target)[executor]?.type)
    }

    // isolation

    @Test
    fun GIVEN_request_to_player_a_WHEN_get_for_player_b_THEN_returns_empty_map() {
        val api = tpaApi()
        val playerA = UUID.randomUUID()
        val playerB = UUID.randomUUID()
        api.tpa(UUID.randomUUID(), playerA)
        assertTrue(api.get(playerB).isEmpty())
        assertFalse(api.isBeingWaited(playerB))
    }
}
