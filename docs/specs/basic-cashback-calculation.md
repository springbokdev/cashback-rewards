# Basic Cashback Calculation

## Story
As a customer, I want to earn cashback on my purchases so that I'm rewarded for
shopping with partner merchants.

## Acceptance Criteria

- Cashback is calculated as a percentage of the purchase amount
- Each merchant has a configured cashback rate
- Cashback is credited to the customer's rewards balance

## Scope Notes

- All merchants in the system are assumed to be valid partners with a configured rate.
  Handling of unregistered merchants is out of scope for this feature.

---

## Rule 1: Must calculate cashback as the merchant's configured rate applied to the purchase amount, rounded down to 2 decimal places

Cashback rates support up to 2 decimal places (e.g. `2.25%`).
Purchases below the `$1.00` minimum do not qualify for cashback.

| Purchase Amount | Merchant Rate | Cashback | Notes                        |
|-----------------|---------------|----------|------------------------------|
| $100.00         | 5.00%         | $5.00    | Standard case                |
| $49.99          | 3.00%         | $1.49    | Fractional cent rounds down  |
| $25.00          | 2.25%         | $0.56    | Fractional rate (2 decimals) |
| $1.00           | 10.00%        | $0.10    | Minimum qualifying amount    |
| $0.99           | 5.00%         | —        | Rejected: below $1.00 minimum |

- Counter-example: The one where the purchase amount is negative — rejected as invalid
- Counter-example: The one where the purchase amount is $0.99 — rejected, below the $1.00 minimum

## Rule 2: Must create cashback in a pending state with a merchant-specific settlement period

Each merchant configures its own settlement period.

- Example: The one where a customer purchases at a merchant with a 30-day settlement — cashback
  is recorded as "pending" with a settlement date 30 days from purchase
- Example: The one where a customer purchases at a merchant with a 7-day settlement — cashback
  is recorded as "pending" with a settlement date 7 days from purchase

## Rule 3: Must credit pending cashback to the customer's available balance once the settlement date is reached

- Example: The one where a pending cashback's settlement date arrives — it moves to "settled"
  and the amount is added to the customer's available balance
- Example: The one where a customer has $10.00 available and a $5.00 pending cashback settles —
  available balance becomes $15.00
- Counter-example: The one where the settlement date has not yet been reached — cashback remains
  pending and is not included in the available balance

## Rule 4: Must track pending and available balances separately for each customer

- Example: The one where a new customer earns their first $5.00 cashback — pending balance is
  $5.00, available balance is $0.00
- Example: The one where a customer has $10.00 available and $3.00 pending — both are visible
  but only $10.00 is the available balance
