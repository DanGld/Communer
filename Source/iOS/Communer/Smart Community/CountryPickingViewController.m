//
//  CountryPickingViewController.m
//  Smart Community
//
//  Created by oren shany on 7/23/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "CountryPickingViewController.h"
#import "countryCell.h"
#import "AppDelegate.h"

@interface CountryPickingViewController ()

@end

@implementation CountryPickingViewController

NSMutableArray *countries;
NSDictionary *countriesDic;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    countries = [NSArray arrayWithObjects:@"Afghanistan", @"Akrotiri", @"Albania", @"Algeria", @"American Samoa", @"Andorra", @"Angola", @"Anguilla", @"Antarctica", @"Antigua and Barbuda", @"Argentina", @"Armenia", @"Aruba", @"Ashmore and Cartier Islands", @"Australia", @"Austria", @"Azerbaijan", @"The Bahamas", @"Bahrain", @"Bangladesh", @"Barbados", @"Bassas da India", @"Belarus", @"Belgium", @"Belize", @"Benin", @"Bermuda", @"Bhutan", @"Bolivia", @"Bosnia and Herzegovina", @"Botswana", @"Bouvet Island", @"Brazil", @"British Indian Ocean Territory", @"British Virgin Islands", @"Brunei", @"Bulgaria", @"Burkina Faso", @"Burma", @"Burundi", @"Cambodia", @"Cameroon", @"Canada", @"Cape Verde", @"Cayman Islands", @"Central African Republic", @"Chad", @"Chile", @"China", @"Christmas Island", @"Clipperton Island", @"Cocos (Keeling) Islands", @"Colombia", @"Comoros", @"Democratic Republic of the Congo", @"Republic of the Congo", @"Cook Islands", @"Coral Sea Islands", @"Costa Rica", @"Cote d'Ivoire", @"Croatia", @"Cuba", @"Cyprus", @"Czech Republic", @"Denmark", @"Dhekelia", @"Djibouti", @"Dominica", @"Dominican Republic", @"Ecuador", @"Egypt", @"El Salvador", @"Equatorial Guinea", @"Eritrea", @"Estonia", @"Ethiopia", @"Europa Island", @"Falkland Islands (Islas Malvinas)", @"Faroe Islands", @"Fiji", @"Finland", @"France", @"French Guiana", @"French Polynesia", @"French Southern and Antarctic Lands", @"Gabon", @"The Gambia", @"Gaza Strip", @"Georgia", @"Germany", @"Ghana", @"Gibraltar", @"Glorioso Islands", @"Greece", @"Greenland", @"Grenada", @"Guadeloupe", @"Guam", @"Guatemala", @"Guernsey", @"Guinea", @"Guinea-Bissau", @"Guyana", @"Haiti", @"Heard Island and McDonald Islands", @"Holy See (Vatican City)", @"Honduras", @"Hong Kong", @"Hungary", @"Iceland", @"India", @"Indonesia", @"Iran", @"Iraq", @"Ireland", @"Isle of Man", @"Israel", @"Italy", @"Jamaica", @"Jan Mayen", @"Japan", @"Jersey", @"Jordan", @"Juan de Nova Island", @"Kazakhstan", @"Kenya", @"Kiribati", @"North Korea", @"South Korea", @"Kuwait", @"Kyrgyzstan", @"Laos", @"Latvia", @"Lebanon", @"Lesotho", @"Liberia", @"Libya", @"Liechtenstein", @"Lithuania", @"Luxembourg", @"Macau", @"Macedonia", @"Madagascar", @"Malawi", @"Malaysia", @"Maldives", @"Mali", @"Malta", @"Marshall Islands", @"Martinique", @"Mauritania", @"Mauritius", @"Mayotte", @"Mexico", @"Federated States of Micronesia", @"Moldova", @"Monaco", @"Mongolia", @"Montserrat", @"Morocco", @"Mozambique", @"Namibia", @"Nauru", @"Navassa Island", @"Nepal", @"Netherlands", @"Netherlands Antilles", @"New Caledonia", @"New Zealand", @"Nicaragua", @"Niger", @"Nigeria", @"Niue", @"Norfolk Island", @"Northern Mariana Islands", @"Norway", @"Oman", @"Pakistan", @"Palau", @"Panama", @"Papua New Guinea", @"Paracel Islands", @"Paraguay", @"Peru", @"Philippines", @"Pitcairn Islands", @"Poland", @"Portugal", @"Puerto Rico", @"Qatar", @"Reunion", @"Romania", @"Russia", @"Rwanda", @"Saint Helena", @"Saint Kitts and Nevis", @"Saint Lucia", @"Saint Pierre and Miquelon", @"Saint Vincent and the Grenadines", @"Samoa", @"San Marino", @"Sao Tome and Principe", @"Saudi Arabia", @"Senegal", @"Serbia", @"Montenegro", @"Seychelles", @"Sierra Leone", @"Singapore", @"Slovakia", @"Slovenia", @"Solomon Islands", @"Somalia", @"South Africa", @"South Georgia and the South Sandwich Islands", @"Spain", @"Spratly Islands", @"Sri Lanka", @"Sudan", @"Suriname", @"Svalbard", @"Swaziland", @"Sweden", @"Switzerland", @"Syria", @"Taiwan", @"Tajikistan", @"Tanzania", @"Thailand", @"Tibet", @"Timor-Leste", @"Togo", @"Tokelau", @"Tonga", @"Trinidad and Tobago", @"Tromelin Island", @"Tunisia", @"Turkey", @"Turkmenistan", @"Turks and Caicos Islands", @"Tuvalu", @"Uganda", @"Ukraine", @"United Arab Emirates", @"United Kingdom", @"United States", @"Uruguay", @"Uzbekistan", @"Vanuatu", @"Venezuela", @"Vietnam", @"Virgin Islands", @"Wake Island", @"Wallis and Futuna", @"West Bank", @"Western Sahara", @"Yemen", @"Zambia", @"Zimbabwe", nil];
    
    countriesDic = @{
                  @"Canada"                                       : @"+1",
                  @"China"                                        : @"+86",
                  @"France"                                       : @"+33",
                  @"Germany"                                      : @"+49",
                  @"India"                                        : @"+91",
                  @"Japan"                                        : @"+81",
                  @"Pakistan"                                     : @"+92",
                  @"United Kingdom"                               : @"+44",
                  @"United States"                                : @"+1",
                  @"Abkhazia"                                     : @"+7 840",
                  @"Abkhazia"                                     : @"+7 940",
                  @"Afghanistan"                                  : @"+93",
                  @"Albania"                                      : @"+355",
                  @"Algeria"                                      : @"+213",
                  @"American Samoa"                               : @"+1 684",
                  @"Andorra"                                      : @"+376",
                  @"Angola"                                       : @"+244",
                  @"Anguilla"                                     : @"+1 264",
                  @"Antigua and Barbuda"                          : @"+1 268",
                  @"Argentina"                                    : @"+54",
                  @"Armenia"                                      : @"+374",
                  @"Aruba"                                        : @"+297",
                  @"Ascension"                                    : @"+247",
                  @"Australia"                                    : @"+61",
                  @"Australian External Territories"              : @"+672",
                  @"Austria"                                      : @"+43",
                  @"Azerbaijan"                                   : @"+994",
                  @"Bahamas"                                      : @"+1 242",
                  @"Bahrain"                                      : @"+973",
                  @"Bangladesh"                                   : @"+880",
                  @"Barbados"                                     : @"+1 246",
                  @"Barbuda"                                      : @"+1 268",
                  @"Belarus"                                      : @"+375",
                  @"Belgium"                                      : @"+32",
                  @"Belize"                                       : @"+501",
                  @"Benin"                                        : @"+229",
                  @"Bermuda"                                      : @"+1 441",
                  @"Bhutan"                                       : @"+975",
                  @"Bolivia"                                      : @"+591",
                  @"Bosnia and Herzegovina"                       : @"+387",
                  @"Botswana"                                     : @"+267",
                  @"Brazil"                                       : @"+55",
                  @"British Indian Ocean Territory"               : @"+246",
                  @"British Virgin Islands"                       : @"+1 284",
                  @"Brunei"                                       : @"+673",
                  @"Bulgaria"                                     : @"+359",
                  @"Burkina Faso"                                 : @"+226",
                  @"Burundi"                                      : @"+257",
                  @"Cambodia"                                     : @"+855",
                  @"Cameroon"                                     : @"+237",
                  @"Canada"                                       : @"+1",
                  @"Cape Verde"                                   : @"+238",
                  @"Cayman Islands"                               : @"+ 345",
                  @"Central African Republic"                     : @"+236",
                  @"Chad"                                         : @"+235",
                  @"Chile"                                        : @"+56",
                  @"China"                                        : @"+86",
                  @"Christmas Island"                             : @"+61",
                  @"Cocos-Keeling Islands"                        : @"+61",
                  @"Colombia"                                     : @"+57",
                  @"Comoros"                                      : @"+269",
                  @"Congo"                                        : @"+242",
                  @"Congo, Dem. Rep. of (Zaire)"                  : @"+243",
                  @"Cook Islands"                                 : @"+682",
                  @"Costa Rica"                                   : @"+506",
                  @"Ivory Coast"                                  : @"+225",
                  @"Croatia"                                      : @"+385",
                  @"Cuba"                                         : @"+53",
                  @"Curacao"                                      : @"+599",
                  @"Cyprus"                                       : @"+537",
                  @"Czech Republic"                               : @"+420",
                  @"Denmark"                                      : @"+45",
                  @"Diego Garcia"                                 : @"+246",
                  @"Djibouti"                                     : @"+253",
                  @"Dominica"                                     : @"+1 767",
                  @"Dominican Republic"                           : @"+1 809",
                  @"Dominican Republic"                           : @"+1 829",
                  @"Dominican Republic"                           : @"+1 849",
                  @"East Timor"                                   : @"+670",
                  @"Easter Island"                                : @"+56",
                  @"Ecuador"                                      : @"+593",
                  @"Egypt"                                        : @"+20",
                  @"El Salvador"                                  : @"+503",
                  @"Equatorial Guinea"                            : @"+240",
                  @"Eritrea"                                      : @"+291",
                  @"Estonia"                                      : @"+372",
                  @"Ethiopia"                                     : @"+251",
                  @"Falkland Islands"                             : @"+500",
                  @"Faroe Islands"                                : @"+298",
                  @"Fiji"                                         : @"+679",
                  @"Finland"                                      : @"+358",
                  @"France"                                       : @"+33",
                  @"French Antilles"                              : @"+596",
                  @"French Guiana"                                : @"+594",
                  @"French Polynesia"                             : @"+689",
                  @"Gabon"                                        : @"+241",
                  @"Gambia"                                       : @"+220",
                  @"Georgia"                                      : @"+995",
                  @"Germany"                                      : @"+49",
                  @"Ghana"                                        : @"+233",
                  @"Gibraltar"                                    : @"+350",
                  @"Greece"                                       : @"+30",
                  @"Greenland"                                    : @"+299",
                  @"Grenada"                                      : @"+1 473",
                  @"Guadeloupe"                                   : @"+590",
                  @"Guam"                                         : @"+1 671",
                  @"Guatemala"                                    : @"+502",
                  @"Guinea"                                       : @"+224",
                  @"Guinea-Bissau"                                : @"+245",
                  @"Guyana"                                       : @"+595",
                  @"Haiti"                                        : @"+509",
                  @"Honduras"                                     : @"+504",
                  @"Hong Kong SAR China"                          : @"+852",
                  @"Hungary"                                      : @"+36",
                  @"Iceland"                                      : @"+354",
                  @"India"                                        : @"+91",
                  @"Indonesia"                                    : @"+62",
                  @"Iran"                                         : @"+98",
                  @"Iraq"                                         : @"+964",
                  @"Ireland"                                      : @"+353",
                  @"Israel"                                       : @"+972",
                  @"Italy"                                        : @"+39",
                  @"Jamaica"                                      : @"+1 876",
                  @"Japan"                                        : @"+81",
                  @"Jordan"                                       : @"+962",
                  @"Kazakhstan"                                   : @"+7 7",
                  @"Kenya"                                        : @"+254",
                  @"Kiribati"                                     : @"+686",
                  @"North Korea"                                  : @"+850",
                  @"South Korea"                                  : @"+82",
                  @"Kuwait"                                       : @"+965",
                  @"Kyrgyzstan"                                   : @"+996",
                  @"Laos"                                         : @"+856",
                  @"Latvia"                                       : @"+371",
                  @"Lebanon"                                      : @"+961",
                  @"Lesotho"                                      : @"+266",
                  @"Liberia"                                      : @"+231",
                  @"Libya"                                        : @"+218",
                  @"Liechtenstein"                                : @"+423",
                  @"Lithuania"                                    : @"+370",
                  @"Luxembourg"                                   : @"+352",
                  @"Macau SAR China"                              : @"+853",
                  @"Macedonia"                                    : @"+389",
                  @"Madagascar"                                   : @"+261",
                  @"Malawi"                                       : @"+265",
                  @"Malaysia"                                     : @"+60",
                  @"Maldives"                                     : @"+960",
                  @"Mali"                                         : @"+223",
                  @"Malta"                                        : @"+356",
                  @"Marshall Islands"                             : @"+692",
                  @"Martinique"                                   : @"+596",
                  @"Mauritania"                                   : @"+222",
                  @"Mauritius"                                    : @"+230",
                  @"Mayotte"                                      : @"+262",
                  @"Mexico"                                       : @"+52",
                  @"Micronesia"                                   : @"+691",
                  @"Midway Island"                                : @"+1 808",
                  @"Micronesia"                                   : @"+691",
                  @"Moldova"                                      : @"+373",
                  @"Monaco"                                       : @"+377",
                  @"Mongolia"                                     : @"+976",
                  @"Montenegro"                                   : @"+382",
                  @"Montserrat"                                   : @"+1664",
                  @"Morocco"                                      : @"+212",
                  @"Myanmar"                                      : @"+95",
                  @"Namibia"                                      : @"+264",
                  @"Nauru"                                        : @"+674",
                  @"Nepal"                                        : @"+977",
                  @"Netherlands"                                  : @"+31",
                  @"Netherlands Antilles"                         : @"+599",
                  @"Nevis"                                        : @"+1 869",
                  @"New Caledonia"                                : @"+687",
                  @"New Zealand"                                  : @"+64",
                  @"Nicaragua"                                    : @"+505",
                  @"Niger"                                        : @"+227",
                  @"Nigeria"                                      : @"+234",
                  @"Niue"                                         : @"+683",
                  @"Norfolk Island"                               : @"+672",
                  @"Northern Mariana Islands"                     : @"+1 670",
                  @"Norway"                                       : @"+47",
                  @"Oman"                                         : @"+968",
                  @"Pakistan"                                     : @"+92",
                  @"Palau"                                        : @"+680",
                  @"Palestinian Territory"                        : @"+970",
                  @"Panama"                                       : @"+507",
                  @"Papua New Guinea"                             : @"+675",
                  @"Paraguay"                                     : @"+595",
                  @"Peru"                                         : @"+51",
                  @"Philippines"                                  : @"+63",
                  @"Poland"                                       : @"+48",
                  @"Portugal"                                     : @"+351",
                  @"Puerto Rico"                                  : @"+1 787",
                  @"Puerto Rico"                                  : @"+1 939",
                  @"Qatar"                                        : @"+974",
                  @"Reunion"                                      : @"+262",
                  @"Romania"                                      : @"+40",
                  @"Russia"                                       : @"+7",
                  @"Rwanda"                                       : @"+250",
                  @"Samoa"                                        : @"+685",
                  @"San Marino"                                   : @"+378",
                  @"Saudi Arabia"                                 : @"+966",
                  @"Senegal"                                      : @"+221",
                  @"Serbia"                                       : @"+381",
                  @"Seychelles"                                   : @"+248",
                  @"Sierra Leone"                                 : @"+232",
                  @"Singapore"                                    : @"+65",
                  @"Slovakia"                                     : @"+421",
                  @"Slovenia"                                     : @"+386",
                  @"Solomon Islands"                              : @"+677",
                  @"South Africa"                                 : @"+27",
                  @"South Georgia and the South Sandwich Islands" : @"+500",
                  @"Spain"                                        : @"+34",
                  @"Sri Lanka"                                    : @"+94",
                  @"Sudan"                                        : @"+249",
                  @"Suriname"                                     : @"+597",
                  @"Swaziland"                                    : @"+268",
                  @"Sweden"                                       : @"+46",
                  @"Switzerland"                                  : @"+41",
                  @"Syria"                                        : @"+963",
                  @"Taiwan"                                       : @"+886",
                  @"Tajikistan"                                   : @"+992",
                  @"Tanzania"                                     : @"+255",
                  @"Thailand"                                     : @"+66",
                  @"Timor Leste"                                  : @"+670",
                  @"Togo"                                         : @"+228",
                  @"Tokelau"                                      : @"+690",
                  @"Tonga"                                        : @"+676",
                  @"Trinidad and Tobago"                          : @"+1 868",
                  @"Tunisia"                                      : @"+216",
                  @"Turkey"                                       : @"+90",
                  @"Turkmenistan"                                 : @"+993",
                  @"Turks and Caicos Islands"                     : @"+1 649",
                  @"Tuvalu"                                       : @"+688",
                  @"Uganda"                                       : @"+256",
                  @"Ukraine"                                      : @"+380",
                  @"United Arab Emirates"                         : @"+971",
                  @"United Kingdom"                               : @"+44",
                  @"United States"                                : @"+1",
                  @"Uruguay"                                      : @"+598",
                  @"U.S. Virgin Islands"                          : @"+1 340",
                  @"Uzbekistan"                                   : @"+998",
                  @"Vanuatu"                                      : @"+678",
                  @"Venezuela"                                    : @"+58",
                  @"Vietnam"                                      : @"+84",
                  @"Wake Island"                                  : @"+1 808",
                  @"Wallis and Futuna"                            : @"+681",
                  @"Yemen"                                        : @"+967",
                  @"Zambia"                                       : @"+260",
                  @"Zanzibar"                                     : @"+255",
                  @"Zimbabwe"                                     : @"+263"
                  };
    
    self.countriesTable.delegate = self;
    self.countriesTable.dataSource = self;
    
    self.filteredCountriesArray = countries;
}

-(void)filterContentForSearchText:(NSString*)searchText scope:(NSString*)scope {
    // Update the filtered array based on the search text and scope.
    // Remove all objects from the filtered search array
    //[self.filteredCountriesArray removeAllObjects];
    // Filter the array using NSPredicate
    NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF contains[c] %@",searchText];
    self.filteredCountriesArray = [NSMutableArray arrayWithArray:[countries filteredArrayUsingPredicate:predicate]];
}

-(BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchString:(NSString *)searchString {
    // Tells the table data source to reload when text changes
    [self filterContentForSearchText:searchString scope:
     [[self.searchDisplayController.searchBar scopeButtonTitles] objectAtIndex:[self.searchDisplayController.searchBar selectedScopeButtonIndex]]];
    // Return YES to cause the search result table view to be reloaded.
    return YES;
}

-(BOOL)searchDisplayController:(UISearchDisplayController *)controller shouldReloadTableForSearchScope:(NSInteger)searchOption {
    // Tells the table data source to reload when scope bar selection changes
    [self filterContentForSearchText:self.searchDisplayController.searchBar.text scope:
     [[self.searchDisplayController.searchBar scopeButtonTitles] objectAtIndex:searchOption]];
    // Return YES to cause the search result table view to be reloaded.
    return YES;
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    if (tableView == self.searchDisplayController.searchResultsTableView) {
        return [self.filteredCountriesArray count];
    } else {
        return [countries count];
    }
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    countryCell *cell = [tableView dequeueReusableCellWithIdentifier:@"countryCell"];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"countryCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
    
    if (tableView == self.searchDisplayController.searchResultsTableView) {
        cell.countryName.text = [self.filteredCountriesArray objectAtIndex:indexPath.row];
    } else {
        cell.countryName.text = [countries objectAtIndex:indexPath.row];
    }
    
    cell.countryCode.text = [countriesDic objectForKey:cell.countryName.text];
    
    return  cell;
    
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    countryCell* cell = (countryCell*)[tableView cellForRowAtIndexPath:indexPath];
    AppDelegate* delegate = [UIApplication sharedApplication].delegate;
    delegate.countryCode = cell.countryCode.text;
    [self dismissViewControllerAnimated:YES completion:nil];
}


- (IBAction)backBtn {
    [self dismissViewControllerAnimated:YES completion:nil];
}
@end