# Merchant Categories & Eligibility

**As the card rewards product manager, I want cashback rates to vary by merchant category and only apply to eligible transactions, so that rewards target the spending we want to encourage.**

A transaction's category comes from its MCC, using the Product Categories setup. The partner-merchant rule from Basic Cashback Calculation still applies: only purchases at partner merchants earn. A transaction that fails any eligibility rule is accepted but silently ignored. No cashback record is created for it.

## Rules and Examples

### Rule: Should award cashback at the rate for the transaction's merchant category, rounded down to whole cents

| Category | MCC | Purchase Amount | Cashback |
|---|---|---|---|
| Groceries | 5411 | $100.00 | $2.00 |
| Fuel | 5541 | $100.00 | $1.00 |
| Groceries | 5411 | $33.33 | $0.66 |

The $33.33 row is the rounding boundary: $0.6666 is rounded down to $0.66, never up.

- **Counter-example:** The one where a supermarket charges at its own fuel station under MCC 5541. It earns the Fuel rate of $1.00 on $100.00, not Groceries, because the category comes from the transaction's MCC, not the merchant's brand.
- **Counter-example:** The one where a customer spends $100.00 at a grocery store that isn't a partner merchant. No cashback record is created.

---

### Rule: Should apply the 0.5% default rate to transactions outside Groceries and Fuel

| MCC | Purchase Amount | Reported Category | Cashback |
|---|---|---|---|
| 5912 (Pharmacy) | $100.00 | Other | $0.50 |
| *(none supplied)* | $100.00 | Other | $0.50 |

The no-MCC row is the boundary: a transaction with no MCC is treated like any unmapped MCC and earns the default rate.

---

### Rule: Must only award cashback on posted transactions

- **Example:** The one where a $50.00 grocery purchase posts and earns $1.00.
- **Example:** The one where a fuel pump pre-authorises $1.00 and the transaction later posts at $45.20. Cashback is calculated on the posted amount: $0.45.
- **Counter-example:** The one where a $50.00 grocery purchase is still pending. No cashback record is created until it posts.
- **Counter-example:** The one where a pending authorisation expires or is voided and never posts. It never earns cashback.

---

### Rule: Must only award cashback when the card was active at the time of purchase

| Card status at purchase | Card status at posting | Cashback on $100.00 Groceries |
|---|---|---|
| Active | Active | $2.00 |
| Active | Frozen | $2.00 |
| Active | Cancelled | $2.00 |
| Frozen | Frozen | $0.00 (no record) |
| Cancelled | Cancelled | $0.00 (no record) |

The "Active → Frozen" and "Active → Cancelled" rows are the boundaries: only the card's status when the customer made the purchase counts. Frozen and cancelled cards decline new authorisations, so the ineligible rows mostly come from recurring payments and merchant force-posts.

---

### Rule: Must only award cashback on purchase transactions

| Transaction type | Earns cashback? |
|---|---|
| Purchase | Yes |
| Refund | No (it still reverses the original cashback, per Basic Cashback Calculation) |
| Fee (annual, late, foreign transaction) | No |
| Cash equivalent (gift card, money order, prepaid top-up, crypto) | No |

- **Counter-example:** The one where a $100.00 grocery purchase abroad has a $3.00 foreign transaction fee. It earns $2.00 on the $100.00 purchase only, and the fee earns nothing.
- **Counter-example:** The one where a refund is made against a purchase that never earned cashback (for example, one made on a frozen card). Nothing is clawed back.
