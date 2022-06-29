package pk.mosafir.travsol.interfaces;

import java.util.List;

import pk.mosafir.travsol.response.GeneralFlightResponse;

public interface SelectedAirportListInterface {
    void selected(List<GeneralFlightResponse> model);
}
