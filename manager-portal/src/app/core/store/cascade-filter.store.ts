import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';

export interface GeoState {
  region: string | null;
  prefecture: string | null;
  commune: string | null;
  canton: string | null;
  startDate: string | null;
  endDate: string | null;
}

const initialState: GeoState = {
  region: null,
  prefecture: null,
  commune: null,
  canton: null,
  startDate: null,
  endDate: null,
};

export const CascadeFilterStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withMethods((store) => ({
    setRegion(region: string | null) {
      patchState(store, { region, prefecture: null, commune: null, canton: null });
    },
    setPrefecture(prefecture: string | null) {
      patchState(store, { prefecture, commune: null, canton: null });
    },
    setCommune(commune: string | null) {
      patchState(store, { commune, canton: null });
    },
    setCanton(canton: string | null) {
      patchState(store, { canton });
    },
    setDateRange(startDate: string | null, endDate: string | null) {
      patchState(store, { startDate, endDate });
    },
    resetFilters() {
      patchState(store, initialState);
    }
  }))
);
