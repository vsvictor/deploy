package d2.polpharma.services.db.service.pharm;


import ic.Service;
import ic.geo.Location;
import ic.geo.Place;
import ic.network.http.HttpException;
import ic.struct.collection.Collection;
import ic.text.Language;
import ic.throwables.AccessDenied;
import ic.throwables.Empty;
import ic.throwables.IOException;

import static d2.polpharma.services.db.model.pharm.Pharmacy.getPharmaciesMapper;
import static ic.geo.Place.findPlacesByAddress;
import static ic.parallel.SleepKt.sleep;
import static ic.parallel.DoInParallelKt.doInParallel;
import static ic.throwables.Break.BREAK;


public class PharmaciesLocationService extends Service {


	@Override protected boolean isReusable() { return false; }


	@Override protected void implementStart() {

		doInParallel(() -> {

			while (!toStop()) {

				getPharmaciesMapper().getItems().forEach(pharmacy -> {

					if (toStop()) throw BREAK;

					if (pharmacy.getLocation() != null) return;

					Collection<Place> places; try {
						places = findPlacesByAddress(
							pharmacy.region + ", " + pharmacy.city + ", " + pharmacy.address,
							Language.UKRAINIAN,
							"AIzaSyD1S25d5AwtpMaDojDUSHP_2idbRTOSbiI"
						);
					} catch (IOException e) 	{ return;
					} catch (HttpException e) 	{
						e.printStackTrace();
						return;
					} catch (AccessDenied accessDenied) {
						accessDenied.printStackTrace();
						throw BREAK;
					}

					try {
						pharmacy.setLocation(places.safeGetAny().location);
					} catch (Empty empty) {
						pharmacy.setLocation(new Location(0, 0));
					}

				});

				sleep(4194304);

			}

		});

	}


	@Override protected void implementStop() {}


}
