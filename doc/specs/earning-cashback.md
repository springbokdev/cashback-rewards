# Earning Cashback on Purchases

## User Story

As a customer
I want to earn cashback on purchases
So that I get money back for spending with partner merchants

## Example Mapping

- Rule: Should earn cashback on a purchase made at an eligible partner merchant
    - Example: The one where a customer buys groceries at a partner supermarket and earns cashback on the full purchase amount
    - Example: The one where a customer makes two separate purchases at partner merchants on the same day and earns cashback on each one independently
    - Counter-example: The one where a customer makes a purchase at a merchant that is not a partner and earns no cashback at all
    - Questions:
        - Is "eligible merchant" defined by a partner list, a merchant category code, or both? (see merchant-categories-and-eligibility.md)
        - Does eligibility need to be checked at the time of purchase or at the time cashback is calculated?

- Rule: Should calculate cashback as a percentage of the purchase amount
    - Example: The one where a customer spends $100 at a merchant with a 2% cashback rate and earns $2.00 cashback
    - Example: The one where a customer spends $49.99 and the cashback amount is rounded to the nearest cent
    - Counter-example: The one where a customer spends $0 (e.g. a fully voucher-covered purchase) and earns $0.00 cashback rather than an error
    - Questions:
        - How exactly is rounding handled — round half up, half even, always down? (see basic-cashback-calculation.md)
        - Which rate applies when a purchase spans multiple product categories?

- Rule: Should only earn cashback on a settled purchase, not a pending or authorized-only one
    - Example: The one where a card authorization later settles and cashback is credited once settlement is confirmed
    - Example: The one where a purchase settles the next business day and cashback is backdated to the original purchase date
    - Counter-example: The one where a purchase is still pending authorization and no cashback has been credited yet, without this being treated as an error
    - Questions:
        - What is the maximum time window a purchase can remain pending before it's dropped?
        - Does the customer see "pending cashback" anywhere, or only settled cashback?

- Rule: Should not earn cashback on a purchase that is fully refunded
    - Example: The one where a customer returns an item for a full refund and the previously earned cashback is reversed
    - Example: The one where a refund happens after the cashback has already been paid out, and the reversal is recorded as a negative adjustment
    - Counter-example: The one where a customer returns part of an order (partial refund) and cashback is reduced proportionally rather than reversed entirely
    - Questions:
        - Is there a time limit after which a refund no longer reverses cashback (e.g. cashback already withdrawn)?
        - How is a proportional/partial refund's cashback adjustment calculated and rounded?

- Rule: Should attribute earned cashback to the customer who made the purchase
    - Example: The one where a purchase made on a customer's own card credits cashback to that customer's account
    - Example: The one where a customer has multiple cards linked to one account and cashback from any of them accumulates in the same account balance
    - Counter-example: The one where a purchase is made on a card reported lost or stolen before the purchase date, and no cashback is earned for that transaction
    - Questions:
        - Can cashback ever be split or shared between multiple customers (e.g. joint account)?
        - What happens to cashback attribution if a card is transferred to a new account holder?

- Rule: Should only earn cashback for a customer with an active, eligible account
    - Example: The one where a customer with a fully verified, active account earns cashback normally
    - Example: The one where a customer's account is reinstated after a temporary suspension and cashback resumes for purchases made after reinstatement
    - Counter-example: The one where a customer's account is suspended (e.g. for fraud review) at the time of purchase and the purchase earns no cashback
    - Questions:
        - Does a suspended account retain previously earned but unpaid cashback, or is it forfeited?
        - Is there a grace period for newly opened accounts before cashback eligibility starts?
