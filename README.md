<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/89017a2d-7273-4b5e-a624-d55e5a06e8a5" />MediVerify

Verify before you trust. MediVerify is an Android app that checks a medicine batch number against a database of fictional demonstration records and reports whether the batch is expired, recalled, counterfeit-flagged, or unknown.

Demo data only. All records are fictional. This app does not verify real medicines and is not a medical or regulatory tool. A database match is not proof that a medicine is authentic, and a missing record does not prove a medicine is counterfeit.

Features
Scan a barcode or QR code with the camera (CameraX + Google ML Kit)
Manual batch number entry when scanning is not possible
Live batch lookup through the Supabase REST API (read-only, encoded query, demo records only)
Expiry check against the current date
Result screen showing medicine name, batch number, expiry date, status and the reason
Scan history and recent scans on the home screen
Teal healthcare theme with colour-coded results
Result colours
Result	Colour	Meaning
VERIFIED, no known alert	Green	Matching demo record, not expired, no recall or counterfeit flag
Expired	Red	Expiry date is before today
Recalled	Red	Record status is recalled
Counterfeit-flagged	Red	Record status is counterfeit-flagged
UNKNOWN / UNVERIFIED	Amber	No matching record, or the record is incomplete. Never treated as counterfeit
Screenshots

Add your screenshots to a screenshots/ folder and uncomment:

<!-- | Home | Result | History | About | |---|---|---|---| | ![Home](screenshots/home.png) | ![Result](screens<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/6a185973-8c9d-4b88-be2e-d6f1f0d0f8b1" />
hots/result.png) | ![History](screenshots/history.png) | ![About](screenshots/about.png) | -->


<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/4b11c32a-8bca-45bb-aade-cdebc8a5ae7e" />
<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/c589c51a-cb98-40a1-927d-e6417965797c" />
<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/d37d87b1-25bf-4196-97f4-6fd908380aeb" />
<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/14b75a79-17a8-4aa8-a0c0-3e108e674d7f" />
<img width="720" height="1600" alt="image" src="https://github.com/user-attachments/assets/094de82c-cf37-409a-aaa8-20829929cbe6" />




Tech stack
Kotlin, Android Studio, Gradle
CameraX and Google ML Kit (barcode scanning)
Supabase (PostgreSQL + REST API)
Setup
1. Clone
bash
git clone https://github.com/<your-username>/MediVerify.git
cd MediVerify
2. Create the Supabase table

In the Supabase SQL editor, create the table and add some fictional records. Adjust types and status values to match what the app expects:

sql
create table if not exists public.medicine_batches (
  id bigint generated always as identity primary key,
  medicine_name text,
  manufacturer text,
  product_identifier text,
  batch_number text not null,
  manufacturing_date date,
  expiry_date date,
  status text,
  is_demo boolean default true
);

alter table public.medicine_batches enable row level security;

create policy "Public read of demo records"
on public.medicine_batches for select
using (is_demo = true);

Sample test batches used during development: PCT2301 (registered), EXP1001 (expired), REC2001 (recalled), UNKNOWN999 (not in the table).

3. Add your Supabase configuration

Create a file named supabase.properties in the project root. It is git-ignored and must never be committed:

properties
SUPABASE_URL=https://<your-project-ref>.supabase.co
SUPABASE_PUBLISHABLE_KEY=<your-publishable-key>

Use only the publishable (anon) key. Never put a secret or service-role key in the app.

4. Build and run
bash
./gradlew assembleDebug        # Windows: .\gradlew.bat assembleDebug

Or open the project in Android Studio and run it on a device or emulator. Camera scanning works best on a real device.

If the project sits inside OneDrive, builds can fail with a file-lock error. Pause sync or move the project outside OneDrive.

How verification works
The batch number comes from a scan or manual entry.
The app queries medicine_batches with an encoded batch-number filter and is_demo=true.
The result is decided from the record's status and expiry date compared with today.
The reason is shown on the result screen.
Project status

This is a working academic prototype.

Tested

Live lookup against known and unknown batches
Build and unit tests
Barcode reading on a phone
Unknown and recalled result display

Not yet tested

Green VERIFIED and red EXPIRED result screens on a device
Manual entry and demo shortcuts end to end
Network failure handling

Known issues

"Unknown" shows in green in the History list and Recent scans chips (should be amber)
About and Home text still mention a bundled/local dataset although the app reads from Supabase
The Scan Medicine button on the home card has low contrast
Limitations
Fictional demo records only; no connection to government or regulator systems
Scanning reads batch text and cannot prove authenticity
An internet connection is required for lookups
Security
Only the Supabase publishable key is used
The app performs read-only requests
supabase.properties is excluded from version control
Author

Harini.V

