# goodweather

Latest update:
- Added error/failure dialogs
- Fixed updating favourites list
- Changed default colours
- Removed en-US locale

Update 1:
- Migrated to OkHTTP which fixed mobile data retrieval (seems like Retrofit has issues with cellular connection on certain phone models)
- Fixed object model type mismatch (OpenWeatherAPI)
- Fixed crash in details due to call on non-ui thread

Description

This is a fully working application where I implemented all the requested tasks plus added some other comfy features.
I added only few tests as I had no time left.

I can speak of technical side of implementation by request.

The included /binary/goodweather.apk is a pre-built app. You can use it or compile the project yourself.

There are some options to play with (at your risk :). Look them up at the top of MainActivity class.

App features are:

- Independent recent/favourites list
- Reordering of recently serched list
- If searching for item which is already in history, it will be updated and reordered (no duplicates)
- GPS location is Coarse by default (can be tweaked in OPTions)
- Search query is smart: you can use city, city with country code, zip, coordinates in form "x y" or "x,y" (or get them from Location button)
- Details view of each location
- Remove items directly from lists
- Add/Remove to/from Favourites from details view (added upon search by default)
- Auto list updates upon app start (favourite list is updated upon each visit) [sorry, no scheduled updated yet]
- Manual update of lists
- Local storage Serialisation/Deserealisation 
- Metric/Imperial units (based on locale, but can be forced from OPTions)
- APP colours, name and units based on Locale (limited set for now)

Known issues and missing features (sorry, had no time):
- No "loading" or "updating" indication (should be some general spinner indicating activity)
- Time is given in UTC (need to deduct timezone from coordinates and offset correctly)


Few technical aspects:
- Content manager as a Data Source
- External data provider with flexible models (implemented with OkHTTP or Retrofit as alternative)
- Separation of internal and external Models to allow easy changes
- FS ser/deser for simplicity and to keep independence. Also preserves existing order without a hassle.
- Fragments for different presentations
- Picasso for hastle-free image loading
