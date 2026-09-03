# Cashback Transaction History

**Story:** As a customer I want to view my cashback transaction history so that I can know where I earn the most cashbacks.

---

## Rule 1: Must display each transaction with date, merchant name, purchase amount, cashback rate, cashback earned, and status (pending/confirmed/expired)

- **Example:** The one where a customer views their history and sees a confirmed transaction showing "2026-08-15, SuperMart, $80.00, 5%, $4.00, Confirmed".
- **Counter-example:** The one where a transaction is still being processed — it appears with a "Pending" status and an estimated cashback amount.

## Rule 2: Must show transactions in reverse chronological order by default, with an option to sort by merchant name

- **Example:** The one where a customer opens their history and the most recent transaction appears first.
- **Example:** The one where a customer sorts by merchant name to group all transactions from the same merchant together.
- **Counter-example:** The one where a customer has no transactions — they see an empty state message.

## Rule 3: Must allow filtering by date range, defaulting to the last 30 days, with up to 1 year of history available

| Filter selected | Transactions shown |
|---|---|
| Default (no filter) | Last 30 days |
| Custom: last 7 days | Only transactions from the past week |
| Custom: last 6 months | Transactions from the past 6 months |
| Custom: 13 months ago | No results — outside the 1-year retention window |

- **Counter-example:** The one where a customer selects a valid date range with no transactions — they see a "no results" message.

## Rule 4: Should show a ranked list of merchants ordered by total cashback earned over the filtered period

- **Example:** The one where a customer sees a ranked list showing they earned $45 at Merchant A, $30 at Merchant B, and $12 at Merchant C in the selected period.
- **Counter-example:** The one where a customer has only transacted at one merchant — the summary shows a single entry.

## Rule 5: Must paginate results at 20 transactions per page

- **Example:** The one where a customer with 55 transactions sees 20 on the first page and can navigate to page 2 and 3.
- **Counter-example:** The one where a customer has 15 transactions — all display on one page with no pagination controls.

## Rule 6: Must only show the authenticated customer's own transactions

- **Example:** The one where a customer logs in and sees only transactions linked to their own account.
- **Counter-example:** The one where a customer filters by a merchant they've never used — no results appear, with no indication of other customers' activity.
