// Foursquare API Info
const clientId = '5MFR2FIONG3CCN5IXQBUE3HFBKEECHZZ0Q1XNZVPZQEZMZDQ';
const clientSecret = 'XVR2PAUVWZSPQY5RY0HZV54ZMBBXM2CWG4RKCGGQUT0JC0OU';
const url = 'https://api.foursquare.com/v2/venues/explore?';
var lat = 17.4175;
var lon = 78.424;
// Page Elements
const $submit = $('#button');
const $container = $('.container');
const $destination = $('#destination');
const $venueDivs = [$("#venue1"), $("#venue2"), $("#venue3"), $("#venue4")];

function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(showPosition);
    }
}
    function showPosition(position) {
        lat = position.coords.latitude;
        lon = position.coords.longitude;
    }
    getLocation()


    // Add AJAX functions here:
    const getVenues = async () => {
        const llval=`${lat},${lon}`;
        const urlToFetch = `${url}ll=${llval}&limit=10&client_id=${clientId}&client_secret=${clientSecret}&v=20181001`;

        try {
            const response = await fetch(urlToFetch);
            if (response.ok) {
                const jsonResponse = await response.json();
                const venues = jsonResponse.response.groups[0].items.map(item => item.venue);
                console.log(venues);
                return venues;
            }
        }
        catch (error) {
            console.log(error);
        }

    };


    // Render functions
    const renderVenues = (venues) => {
        $venueDivs.forEach(($venue, index) => {
            const venue = venues[index];
            const venueIcon = venue.categories[0].icon;
            const venueImgSrc = `${venueIcon.prefix}bg_64${venueIcon.suffix}`;
            let venueContent = createVenueHTML(venue.name, venue.location, venueImgSrc);
            $venue.append(venueContent);
        });
        $destination.append(`<h2>${venues[0].location.city}</h2>`);
    };


    const executeSearch = () => {
        $venueDivs.forEach(venue => venue.empty());
        $destination.empty();
        $container.css("visibility", "visible");
        getVenues().then(venues => renderVenues(venues));
        return false;
    };

    $submit.click(executeSearch)
