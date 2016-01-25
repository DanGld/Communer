//
//  SingleEventViewController.m
//  Smart Community
//
//  Created by oren shany on 8/5/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "SingleEventViewController.h"
#import "ParticipantCellCollectionViewCell.h"
#import "UIImageView+AFNetworking.h"
#import "CommunityContactModel.h"
#import "Networking.h"
#import "UIImageView+AFNetworking.h"
#import "DataManagement.h"
#import "LogicServices.h"
@import MapKit;
@import EventKit;


@interface SingleEventViewController ()

@end

@implementation SingleEventViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [_scrollContainer setContentSize:CGSizeMake([UIScreen mainScreen].bounds.size.width, 560)];
    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    [dateFormatter setTimeStyle:NSDateFormatterNoStyle];
    NSTimeZone *tz = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
    [dateFormatter setTimeZone:tz];
    NSString *formattedDateString = [dateFormatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:_event.sTime/1000]];
    
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    [formatter setDateFormat:@"HH:mm"];
    [formatter setTimeZone:tz];
    NSString *startTimeString = [formatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:_event.sTime/1000]];
    NSString *endTimeString = [formatter stringFromDate:[[NSDate alloc]initWithTimeIntervalSince1970:_event.eTime/1000]];
    
    if (formattedDateString && startTimeString && endTimeString) {
        _eventDate.text = formattedDateString;
        _eventTime.text = [NSString stringWithFormat:@"%@-%@" , startTimeString, endTimeString];
    }
    
    _eventName.text = _event.name;
    _eventContent.text = _event.content;
    NSString* stringUrl = [self createMapUrlWithCoords:_event.locationObj.coords];
    NSURL *mapUrl = [NSURL URLWithString:[self URLEncodeString:stringUrl]];
    NSURLRequest *requestObj = [NSURLRequest requestWithURL:mapUrl];
    [_mapWebView loadRequest:requestObj];
    
    self.participantsCollection.delegate = self;
    self.participantsCollection.dataSource = self;
    [self.participantsCollection registerNib:[UINib nibWithNibName:@"ParticipantCellCollectionViewCell" bundle:[NSBundle mainBundle]]
          forCellWithReuseIdentifier:@"ParticipantCellCollectionViewCell"];
    if (_event.isFree) {
        [_buyTicketButton setTitle:@"FREE" forState:UIControlStateDisabled];
        [_buyTicketButton.layer setMasksToBounds:YES];
        [_buyTicketButton.layer setCornerRadius:4.0f];
        [_buyTicketButton setEnabled:NO];
    }
    if (self.event.participants.count==0) {
        [_noParticipantsLabel setHidden:NO];
    }
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSString* appLanguage = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([appLanguage isEqualToString:@"hebrew"]) {
        [_titleLabel setText:@"אירוע"];
        [_addToCalenderButton setTitle:@"הוסף ליומן שלי" forState:UIControlStateNormal];
        [_attendingButton setTitle:@"משתתף?" forState:UIControlStateNormal];
        [_participantsLabel setText:@"משתתפים"];
        [_buyTicketButton setTitle:@"רכוש כרטיס" forState:UIControlStateNormal];
        [_areYouAttendingLabel setText:@"האם תשתתף/י באירוע?"];
        [_popupYesLabel setText:@"כן"];
        [_popupMaybeTitle setText:@"אולי"];
        [_popupNoTitle setText:@"לא"];
        [_popupOkButton setTitle:@"אישור" forState:UIControlStateNormal];
        [_popupCancelButton setTitle:@"ביטול" forState:UIControlStateNormal];
    }
}


-(NSString*)createMapUrlWithCoords:(NSString*)coords {
    if ([coords isEqualToString:@""]) { //put community location
        coords = [[LogicServices sharedInstance] getCurrentCommunity].locationObj.coords;
    }
    return [NSString stringWithFormat:@"https://maps.googleapis.com/maps/api/staticmap?center=%@&zoom=16&size=600x300&maptype=roadmap&markers=color:red|%@" , coords, coords];
}

-(NSString *) URLEncodeString:(NSString *) str
{
    
    NSMutableString *tempStr = [NSMutableString stringWithString:str];
    [tempStr replaceOccurrencesOfString:@" " withString:@"+" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [tempStr length])];
    
    
    return [[NSString stringWithFormat:@"%@",tempStr] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
}


-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.event.participants.count;
}

- (void)shareText:(NSString *)text andImage:(UIImage *)image andUrl:(NSURL *)url
{
    NSMutableArray *sharingItems = [NSMutableArray new];
    if (text) {
        [sharingItems addObject:text];
    }
    if (image) {
        [sharingItems addObject:image];
    }
    if (url) {
        [sharingItems addObject:url];
    }
    
    UIActivityViewController *activityController = [[UIActivityViewController alloc] initWithActivityItems:sharingItems applicationActivities:nil];
    [self presentViewController:activityController animated:YES completion:nil];
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    ParticipantCellCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"ParticipantCellCollectionViewCell" forIndexPath:indexPath];
    CommunityContactModel* participant =  self.event.participants[indexPath.row];
    [cell.imageView setImageWithURL:[NSURL URLWithString:participant.imageUrl] placeholderImage:[UIImage imageNamed:@"communer_placeholder"]];
    return cell;
}


- (IBAction)shareClicked {
    NSString* shareText = [NSString stringWithFormat:@"%@\n\n%@\n, %@  %@, %@\nApp Store link: http://itunes.com/apps/communer" , _event.name, _event.content, _eventTime.text, _eventDate.text,_event.locationObj.title];
    NSURL *url = [NSURL URLWithString:(_event.imageUrl)];
    NSData *data = [NSData dataWithContentsOfURL:url];
    [self shareText:shareText andImage:[UIImage imageWithData: data] andUrl:nil];
}

- (IBAction)attendingClicked {
    if (_event.attendingStatus && ![_event.attendingStatus isEqualToString:@""])
    {
        if ([_event.attendingStatus isEqualToString:@"yes"]) {
            [self attendingYesClicked];
        }
        if ([_event.attendingStatus isEqualToString:@"maybe"]) {
            [self attendingMaybeClicked];
        }
        if ([_event.attendingStatus isEqualToString:@"no"]) {
            [self attendingNoClicked];
        }
    }
    [UIView animateWithDuration:0.3 animations:^{
        [_AttendingView setAlpha:1];
        [_blackBGLayer setAlpha:0.5];
    }];
    
}
- (IBAction)setAttendingStatusClicked {
    [[Networking sharedInstance] setAttendingStatus:_event.attendingStatus ForEventID:_event.eventID];
    [self closeAttendingView];
    if ([_event.attendingStatus isEqualToString:@"yes"]) {
        CommunityContactModel* mySelf = [[CommunityContactModel alloc]init];
        mySelf.imageUrl = [DataManagement sharedInstance].user.imageUrl;
        [_event.participants addObject:mySelf];
        [_noParticipantsLabel setHidden:YES];
        [_participantsCollection reloadData];
    }
}


- (IBAction)closeAttendingView {
    [UIView animateWithDuration:0.3 animations:^{
        [_AttendingView setAlpha:0];
        [_blackBGLayer setAlpha:0];
    }];
}
- (IBAction)attendingYesClicked {
    [_yesCheckbox setImage:[UIImage imageNamed:@"radio_button_on"] forState:UIControlStateNormal];
    [_maybeCheckbox setImage:[UIImage imageNamed:@"radio_button_off"] forState:UIControlStateNormal];
    [_noCheckbox setImage:[UIImage imageNamed:@"radio_button_off"] forState:UIControlStateNormal];
    
    _event.attendingStatus = @"yes";
}
- (IBAction)attendingMaybeClicked {
    [_yesCheckbox setImage:[UIImage imageNamed:@"radio_button_off"] forState:UIControlStateNormal];
    [_maybeCheckbox setImage:[UIImage imageNamed:@"radio_button_on"] forState:UIControlStateNormal];
    [_noCheckbox setImage:[UIImage imageNamed:@"radio_button_off"] forState:UIControlStateNormal];
    _event.attendingStatus = @"maybe";
}
- (IBAction)attendingNoClicked {
    [_yesCheckbox setImage:[UIImage imageNamed:@"radio_button_off"] forState:UIControlStateNormal];
    [_maybeCheckbox setImage:[UIImage imageNamed:@"radio_button_off"] forState:UIControlStateNormal];
    [_noCheckbox setImage:[UIImage imageNamed:@"radio_button_on"] forState:UIControlStateNormal];
    _event.attendingStatus = @"no";
}

- (IBAction)backBtn {
        [self dismissViewControllerAnimated:YES completion:nil];
}
- (IBAction)addToCalender {
    
    EKEventStore *eventStore = [[EKEventStore alloc] init];
    [eventStore requestAccessToEntityType:EKEntityTypeEvent completion:^(BOOL granted, NSError *error) {
        if(granted) {
            EKEvent *event  = [EKEvent eventWithEventStore:eventStore];
            event.title     = _event.name;
            event.notes     = _event.content;
            event.startDate = [[NSDate alloc]initWithTimeIntervalSince1970:_event.sTime/1000];
            event.endDate = [[NSDate alloc]initWithTimeIntervalSince1970:_event.eTime/1000];
            
            float latitude = [[_event.locationObj.coords componentsSeparatedByString:@","][0] floatValue];
            float longitude = [[_event.locationObj.coords componentsSeparatedByString:@","][1] floatValue];
            EKStructuredLocation* structuredLocation = [EKStructuredLocation locationWithTitle:@"Location"];
            CLLocation* location = [[CLLocation alloc] initWithLatitude:latitude longitude:longitude];
            structuredLocation.geoLocation = location;
            event.structuredLocation = structuredLocation;
            [event setCalendar:[eventStore defaultCalendarForNewEvents]];
            NSError *err;
            [eventStore saveEvent:event span:EKSpanThisEvent error:&err];
            if (err) {
                NSLog(@"%@" , err.description);
            }
            else {
                [self performSelectorOnMainThread:@selector(showCalenderAlert) withObject:nil waitUntilDone:YES];
            }
        }}];
    }

-(void)showCalenderAlert {
    NSString* appLanguage = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([appLanguage isEqualToString:@"english"]) {
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"New Event" message:[NSString stringWithFormat:@"%@ event has been saved to your calendar" , _event.name] delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];
    }
    if ([appLanguage isEqualToString:@"hebrew"]) {
        UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"אירוע חדש" message:[NSString stringWithFormat:@"האירוע %@ נשמר ביומן" , _event.name] delegate:self cancelButtonTitle:nil otherButtonTitles:@"אישור", nil];
        [alert show];
    }
}

-(NSDate *) dateWithHour:(NSInteger)hour
                  minute:(NSInteger)minute
                  second:(NSInteger)second
{
    NSCalendar *calendar = [NSCalendar currentCalendar];
    NSDateComponents *components = [calendar components: NSYearCalendarUnit|
                                    NSMonthCalendarUnit|
                                    NSDayCalendarUnit
                                               fromDate:[[NSDate alloc]initWithTimeIntervalSince1970:_event.date/1000]];
    [components setHour:hour];
    [components setMinute:minute];
    [components setSecond:second];
    NSDate *newDate = [calendar dateFromComponents:components];
    return newDate;
}

- (IBAction)buyTicketAction:(id)sender
{
    if (  _event.buyLink != nil  && ![_event.buyLink isKindOfClass:[NSNull class]] && ![_event.buyLink isEqualToString:@""]) {
        NSURL *url = [NSURL URLWithString:_event.buyLink];
        [[UIApplication sharedApplication] openURL:url];
    }
}

- (IBAction)openMaps {
        // Create an MKMapItem to pass to the Maps app
        CLLocationCoordinate2D coordinate =
        CLLocationCoordinate2DMake([[_event.locationObj.coords componentsSeparatedByString:@","][0] doubleValue] , [[_event.locationObj.coords componentsSeparatedByString:@","][1] doubleValue]);
        MKPlacemark *placemark = [[MKPlacemark alloc] initWithCoordinate:coordinate
                                                       addressDictionary:nil];
        MKMapItem *mapItem = [[MKMapItem alloc] initWithPlacemark:placemark];
        [mapItem setName:_event.locationObj.title];
        // Pass the map item to the Maps app
        [mapItem openInMapsWithLaunchOptions:nil];
}

@end
