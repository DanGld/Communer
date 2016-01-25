//
//  SecurityViewController.m
//  Smart Community
//
//  Created by oren shany on 9/2/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "SecurityViewController.h"
#import "SecurityCell.h"
#import "ReportModel.h"
#import "LocationModel.h"
#import "CreateReportViewController.h"
#import "LogicServices.h"
#import "AppDelegate.h"
#import "SingleSecurityController.h"

@interface SecurityViewController ()

@end

@implementation SecurityViewController
{
    BOOL isBottomViewUp;
}

- (void)viewDidLoad {
    [super viewDidLoad];
    _reportsArray = [[LogicServices sharedInstance] getSecurityReportArray];
    if (_reportsArray.count>0)
    [self setBottomViewWithReport:_reportsArray[0]];
    
    if (_needToOpenCreateReport) {
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self createReport];
        });
    }
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"hebrew"]) {
        [_titleLabel setText:@"ביטחון"];
        [_mapViewLabel setText:@"מפת דיווחים"];
        [_reportListLabel setText:@"רשימת דיווחים"];
    }
}

-(void)viewDidAppear:(BOOL)animated
{
    for (int i = 0; i< _reportsArray.count; i++) {
        SecurityEventModel *report = [_reportsArray objectAtIndex:i];
        NSArray *coords = [report.report.location.coords componentsSeparatedByString:@","];
        if (coords.count==2) {
        NSString *lat = [coords objectAtIndex:0];
        NSString *lng = [coords objectAtIndex:1];
        [self updateMapView:lat andLng:lng andReport:report andIndex:i];
        }
    }
    NSString* currentCoords = ((AppDelegate*)[[UIApplication sharedApplication]delegate]).currentLocation.coords;
    double lati = [[currentCoords componentsSeparatedByString:@":"][0] doubleValue];
    double longi = [[currentCoords componentsSeparatedByString:@":"][1] doubleValue];
    
    MKCoordinateRegion viewRegion = MKCoordinateRegionMakeWithDistance(CLLocationCoordinate2DMake(lati, longi), 3000, 3000);
    MKCoordinateRegion adjustedRegion = [self.mapView regionThatFits:viewRegion];
    [self.mapView setRegion:adjustedRegion animated:YES];
    self.mapView.showsUserLocation = YES;
}

- (IBAction)mapViewClicked {
    [_mapViewLabel setAlpha:1];
    [_mapViewUnderLine setHidden:NO];
    [_reportListLabel setAlpha:0.5];
    [_reportListUnderline setHidden:YES];
    [_mapView setHidden:NO];
    [UIView animateWithDuration:0.5 animations:^{
        _mapView.layer.opacity = 1.0;
         _securityButton.layer.opacity = 1.0;
        _reportsTableView.layer.opacity = 0.0;
    } completion:^(BOOL finished) {
        [_reportsTableView setHidden:YES];
    }];
}

- (IBAction)reportListClicked {
    [_mapViewLabel setAlpha:0.5];
    [_mapViewUnderLine setHidden:YES];
    [_reportListLabel setAlpha:1];
    [_reportListUnderline setHidden:NO];
    [_reportsTableView setHidden:NO];
    [UIView animateWithDuration:0.5 animations:^{
        _mapView.layer.opacity = 0.0;
        _securityButton.layer.opacity = 0.0;
        _reportsTableView.layer.opacity = 1.0;
    } completion:^(BOOL finished) {
        [_mapView setHidden:YES];
        
    }];
}

- (IBAction)createReport {
    CreateReportViewController* createReportController  = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"CreateReportViewController"];
    [self presentViewController:createReportController animated:YES completion:nil];
}

- (IBAction)backButtonClicked {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (void)updateMapView:(NSString *)lat andLng:(NSString *)lng andReport:(SecurityEventModel*)report andIndex:(int)index
{
    // create a region and pass it to the Map View
    MKCoordinateRegion region;
    region.center.latitude = [lat doubleValue];
    region.center.longitude = [lng doubleValue];
    region.span.latitudeDelta = 1.001;
    region.span.longitudeDelta = 1.001;
    
    MKPointAnnotation *point = [[MKPointAnnotation alloc] init];
    MKAnnotationView *annot = [[MKAnnotationView alloc]init];
    annot.tag = index;
    point.coordinate = region.center;
    point.title = [NSString stringWithFormat:@"%i", index];
    [_mapView addAnnotation:point];
}

- (MKAnnotationView *)mapView:(MKMapView *)theMapView viewForAnnotation:(id <MKAnnotation>)annotation {
    if ([annotation isKindOfClass:[MKUserLocation class]]) {
        return nil;
    }
    
    static NSString *SFAnnotationIdentifier = @"SFAnnotationIdentifier";
    MKPinAnnotationView *pinView =
    (MKPinAnnotationView *)[_mapView dequeueReusableAnnotationViewWithIdentifier:SFAnnotationIdentifier];
    if (!pinView)
    {
        MKAnnotationView *annotationView = [[MKAnnotationView alloc] initWithAnnotation:annotation
                                                                         reuseIdentifier:SFAnnotationIdentifier];
        SecurityEventModel *report = [_reportsArray objectAtIndex:[annotation.title intValue]];
        if ([report.report.severity isEqualToString:@"low"]) {
            UIImage *flagImage = [UIImage imageNamed:@"map_green_report"];
            annotationView.image = flagImage;
            [annotationView setFrame:CGRectMake(annotationView.frame.origin.x, annotationView.frame.origin.y, annotationView.frame.size.width/2, annotationView.frame.size.height/2)];
            return annotationView;
        }
        if ([report.report.severity isEqualToString:@"medium"]) {
            UIImage *flagImage = [UIImage imageNamed:@"map_yellow_report"];
            annotationView.image = flagImage;
            [annotationView setFrame:CGRectMake(annotationView.frame.origin.x, annotationView.frame.origin.y, annotationView.frame.size.width/2, annotationView.frame.size.height/2)];
            return annotationView;
        }
        if ([report.report.severity isEqualToString:@"high"]) {
            UIImage *flagImage = [UIImage imageNamed:@"map_red_report"];
            annotationView.image = flagImage;
            [annotationView setFrame:CGRectMake(annotationView.frame.origin.x, annotationView.frame.origin.y, annotationView.frame.size.width/2, annotationView.frame.size.height/2)];
            return annotationView;
        }
        
        //green pin for the default
        UIImage *flagImage = [UIImage imageNamed:@"map_green_report"];
        annotationView.image = flagImage;
        [annotationView setFrame:CGRectMake(annotationView.frame.origin.x, annotationView.frame.origin.y, annotationView.frame.size.width/2, annotationView.frame.size.height/2)];
        return annotationView;
    }
    else
    {
        pinView.annotation = annotation;
    }
    return pinView;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return _reportsArray.count;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return 120;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    SecurityCell *cell = [tableView dequeueReusableCellWithIdentifier:@"SecurityCell"];
    if (cell == nil) {
        // Load the top-level objects from the custom cell XIB.
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"SecurityCell" owner:self options:nil];
        // Grab a pointer to the first object (presumably the custom cell, as that's all the XIB should contain).
        cell = [topLevelObjects objectAtIndex:0];
    }
    SecurityEventModel *report = [_reportsArray objectAtIndex:indexPath.row];
    [cell initWithReport:report];
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
    SecurityEventModel* report = _reportsArray[indexPath.row];
    SingleSecurityController* controller = [[UIStoryboard storyboardWithName:@"Main" bundle:nil] instantiateViewControllerWithIdentifier:@"SingleSecurityController"];
    [self presentViewController:controller animated:YES completion:nil];
     [controller initWithReport:report];
}

- (void)mapView:(MKMapView *)mapView didSelectAnnotationView:(MKAnnotationView *)view
{
    int index = [view.annotation.title intValue];
    if(index != 0)
    {
        SecurityEventModel* report = _reportsArray[index];
        [self setBottomViewWithReport:report];
        if (!isBottomViewUp) {
            [self bottomViewUp];
        }

        MKMapRect r = [mapView visibleMapRect];
        MKMapPoint pt = MKMapPointForCoordinate([view.annotation coordinate]);
        r.origin.x = pt.x - r.size.width * 0.5;
        r.origin.y = pt.y - r.size.height * 0.25;
        [mapView setVisibleMapRect:r animated:YES];
    }
}

- (void)mapView:(MKMapView *)mapView didDeselectAnnotationView:(MKAnnotationView *)view
{
    if (isBottomViewUp) {
        [self bottomViewDown];
    }
}

- (void)setBottomViewWithReport:(SecurityEventModel *)securityEvent
{
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];

    if (securityEvent.report.type && ![securityEvent.report.type isEqualToString:@""]) {
        _typeLabel.text = securityEvent.report.type;
    }
    else {
        _typeLabel.text = @"No Type";
        if ([language isEqualToString:@"hebrew"]) {
            [_typeLabel setText:@"לא מסווג"];
        }
    }
    if ([language isEqualToString:@"english"]) {
        _reportTitle.text = [NSString stringWithFormat:@"Has been reported by %@ at %@" , securityEvent.reporterName.length>0 ? securityEvent.reporterName : @"annonymous", securityEvent.report.location.title];
    _reportDesc.text = securityEvent.report.desc;
    _policeReportedLabel.text = securityEvent.report.isPoliceReport ? @"The police have been reported" : @"The police haven't been reported yet";
    }
    if ([language isEqualToString:@"hebrew"]) {
        _reportTitle.text = [NSString stringWithFormat:@"דווח על ידי %@ ב %@" , securityEvent.reporterName.length>0 ? securityEvent.reporterName : @"אנונימי" , securityEvent.report.location.title];
        _reportDesc.text = securityEvent.report.desc;
        _policeReportedLabel.text = securityEvent.report.isPoliceReport ? @"המשטרה קיבלה דיווח" : @"המשטרה לא קיבלה דיווח";
        [_descriptionLabel setText:@"תיאור"];
    }
}

- (void)bottomViewDown
{
    isBottomViewUp = NO;
    [UIView animateWithDuration:0.5 animations:^{
        _bottomView.frame = CGRectMake(_bottomView.frame.origin.x, (_bottomView.frame.origin.y + _bottomView.frame.size.height), _bottomView.frame.size.width, _bottomView.frame.size.height);
    }];
}

- (void)bottomViewUp
{
    isBottomViewUp = YES;
    [UIView animateWithDuration:0.5 animations:^{
        _bottomView.frame = CGRectMake(_bottomView.frame.origin.x, (_bottomView.frame.origin.y - _bottomView.frame.size.height), _bottomView.frame.size.width, _bottomView.frame.size.height);
    }];
}

@end
