// Foursquare API Info
const clientId = '5MFR2FIONG3CCN5IXQBUE3HFBKEECHZZ0Q1XNZVPZQEZMZDQ';
const clientSecret = 'XVR2PAUVWZSPQY5RY0HZV54ZMBBXM2CWG4RKCGGQUT0JC0OU';
const url = 'https://api.foursquare.com/v2/venues/explore?near=';

// Page Elements
const $input = $('#city');
const $submit = $('#button');
const $destination = $('#destination');
const $container = $('.container');
const $venueDivs = [$("#venue1"), $("#venue2"), $("#venue3"), $("#venue4")];

// Add AJAX functions here:
const getVenues = async () => {
const city = $input.val();
const urlToFetch = `${url}${city}&limit=10&client_id=${clientId}&client_secret=${clientSecret}&v=20181001`;

try {
  const response = await fetch(urlToFetch);
  if (response.ok){
    const jsonResponse = await response.json();
    const venues = jsonResponse.response.groups[0].items.map(item => item.venue);
    console.log(venues);
    return venues;
  }
}
  catch(error){
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
