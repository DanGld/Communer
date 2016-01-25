//
//  CommunityProfileViewController.m
//  Smart Community
//
//  Created by oren shany on 9/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import "CommunityProfileViewController.h"
#import "LogicServices.h"
#import "UIImageView+AFNetworking.h"
#import "ParticipantCellCollectionViewCell.h"
#import "Networking.h"
#import "AllMembersController.h"

@interface CommunityProfileViewController ()

@end

@implementation CommunityProfileViewController
NSString* object;

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(AddCommuitySuccess)
                                                 name:@"addCommuitySuccess"
                                               object:object];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(AddCommuityFail)
                                                 name:@"addCommuityFail"
                                               object:object];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(AddCommuitySuccess)
                                                 name:@"removeCommuitySuccess"
                                               object:object];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(AddCommuityFail)
                                                 name:@"removeCommuityFail"
                                               object:object];


    if ([DataManagement sharedInstance].user.communities.count<2) {
        [_leaveCommunityButton setEnabled:NO];
    }
    if (![[[LogicServices sharedInstance] getCurrentCommunity].memberType isEqualToString:@"guest"]) {
        [_beMemberButton setEnabled:NO];
    }
    
    [_scrollView setContentSize:_viewInScroll.frame.size];
    CommunityModel* currentCommunity = [[LogicServices sharedInstance] getCurrentCommunity];
    [_communityImage setImageWithURL:[NSURL URLWithString:currentCommunity.communityImageUrl] placeholderImage:[UIImage imageNamed:@"placeHolder"]];
    [_communityNameLabel setText:currentCommunity.communityName];
    [_communityMembersCollection setDelegate: self];
    [_communityMembersCollection setDataSource:self];
    [self.communityMembersCollection registerNib:[UINib nibWithNibName:@"ParticipantCellCollectionViewCell" bundle:[NSBundle mainBundle]]
                  forCellWithReuseIdentifier:@"ParticipantCellCollectionViewCell"];
    NSString* stringUrl = [self createMapUrlWithCoords:currentCommunity.locationObj.coords];
    NSURL *mapUrl = [NSURL URLWithString:[self URLEncodeString:stringUrl]];
    NSURLRequest *requestObj = [NSURLRequest requestWithURL:mapUrl];
    [_communityLocationWebview loadRequest:requestObj];
    if (currentCommunity.contacts.count>0) {
        for (CommunityContactModel* contact in currentCommunity.contacts) {
            if ([contact.position isEqualToString:@"Administrator"]) {
                UIImageView* imageView = [[UIImageView alloc]init];
                [imageView setImageWithURL:[NSURL URLWithString:[[LogicServices sharedInstance]getCurrentCommunity].communityImageUrl]];
                [_communityContactImage setImageWithURL:[NSURL URLWithString:contact.imageUrl] placeholderImage:imageView.image];
                [_communityContactImage.layer setMasksToBounds:YES];
                [_communityContactImage.layer setCornerRadius:20.f];
                [_communityContactNameLabel setText:contact.name];
                [_communityContactPositionLabel setText:contact.position];
                [_communityContactPhoneLabel setText:contact.phoneNumber];
            }
        }
    }
    
    [_communityFaxLabel setText:currentCommunity.fax];
    [_communityMailLabel setText:currentCommunity.email];
    [_communityWebsiteLabel setText:currentCommunity.website];
    [_locationTitleLabel setText:currentCommunity.locationObj.title];
}

-(void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    NSString* language = [[NSUserDefaults standardUserDefaults] objectForKey:@"appLanguage"];
    if ([language isEqualToString:@"hebrew"]) {
        [_titleLabel setText:@"פרופיל קהילה"];
        [_nameLabel setText:@"שם"];
        [_membersLabel setText:@"חברים"];
        [_locationLabel setText:@"מיקום"];
        [_contactDetailsLabel setText:@"פרטי קשר"];
        [_faxLabel setText:@"פקס"];
        [_emailLabel setText:@"דוא״ל"];
        [_websiteLabel setText:@"אתר"];
        [_beMemberButton setTitle:@"הצטרפות כחבר" forState: UIControlStateNormal];
        [_leaveCommunityButton setTitle:@"עזיבת קהילה" forState: UIControlStateNormal];
    }
}

-(void)AddCommuitySuccess {
    [_loader startAnimating];
    [UIView animateWithDuration:0.3 animations:^{
        [_loaderView setAlpha:0];
    }];
    [self dismissViewControllerAnimated:YES completion:nil];
}

-(void)AddCommuityFail {
    [_loader startAnimating];
    [UIView animateWithDuration:0.3 animations:^{
        [_loaderView setAlpha:0];
    }];
    UIAlertView* alert = [[UIAlertView alloc]initWithTitle:@"Server error" message:@"Oops.. something went wrong, try again" delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
    [alert show];
}


-(NSString *) URLEncodeString:(NSString *) str
{
    
    NSMutableString *tempStr = [NSMutableString stringWithString:str];
    [tempStr replaceOccurrencesOfString:@" " withString:@"+" options:NSCaseInsensitiveSearch range:NSMakeRange(0, [tempStr length])];
    
    
    return [[NSString stringWithFormat:@"%@",tempStr] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
}

-(NSString*)createMapUrlWithCoords:(NSString*)coords {
    if ([coords isEqualToString:@""]) { //put community location
        coords = [[LogicServices sharedInstance] getCurrentCommunity].locationObj.coords;
    }
    return [NSString stringWithFormat:@"https://maps.googleapis.com/maps/api/staticmap?center=%@&zoom=16&size=350x170&maptype=roadmap&markers=color:red|%@" , coords, coords];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)backButtonClicked {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)callCommunityContact {
    NSString *phoneNumber = _communityContactPhoneLabel.text;    NSString *phoneURLString = [NSString stringWithFormat:@"tel:%@", phoneNumber];
    NSURL *phoneURL = [NSURL URLWithString:phoneURLString];
    [[UIApplication sharedApplication] openURL:phoneURL];
}

- (IBAction)applyToBeMember {
    [_loader startAnimating];
    [UIView animateWithDuration:0.3 animations:^{
        [_loaderView setAlpha:0.5];
    }];
    [[Networking sharedInstance] sendJoinRequestAsGuest:NO ForCommunityID:[[LogicServices sharedInstance] getCurrentCommunity].communityID];
}

-(NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return [[LogicServices sharedInstance] getCurrentCommunity].members.count;
}

-(UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    ParticipantCellCollectionViewCell *cell = [collectionView dequeueReusableCellWithReuseIdentifier:@"ParticipantCellCollectionViewCell" forIndexPath:indexPath];
    CommunityContactModel* participant =  [[LogicServices sharedInstance] getCurrentCommunity].members[indexPath.row];
    NSURLRequest *imageRequest = [NSURLRequest requestWithURL:[NSURL URLWithString:participant.imageUrl]
                                                  cachePolicy:NSURLRequestReturnCacheDataElseLoad
                                              timeoutInterval:60];
    [cell.imageView setImageWithURLRequest:imageRequest
                            placeholderImage:[UIImage imageNamed:@"communer_placeholder"]
                                     success:nil
                                     failure:nil];
    return cell;
}


- (IBAction)leaveCommunityClicked {
    [_loader startAnimating];
    [UIView animateWithDuration:0.3 animations:^{
        [_loaderView setAlpha:0.5];
    }];
    [[Networking sharedInstance] sendLeaveRequestForCommunityID:[[LogicServices sharedInstance] getCurrentCommunity].communityID];
}

- (IBAction)emailClicked:(id)sender {
    MFMailComposeViewController* controller = [[MFMailComposeViewController alloc] init];
    controller.mailComposeDelegate = self;
    [controller setSubject:@""];
    [controller setToRecipients:[NSArray arrayWithObject:_communityMailLabel.text]];
    [controller setMessageBody:@"" isHTML:NO];
    if (controller) [self presentViewController:controller animated:YES completion:nil];
}

-(void)mailComposeController:(MFMailComposeViewController*)controller didFinishWithResult:(MFMailComposeResult)result error:(nullable NSError *)error {
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (IBAction)websiteClicked {
    if (_communityWebsiteLabel.text) {
    [[UIApplication sharedApplication] openURL:[NSURL URLWithString: _communityWebsiteLabel.text]];
    }
}

- (IBAction)showAllMembersClicked {
    AllMembersController *membersController = [[UIStoryboard storyboardWithName:@"Main" bundle:nil]instantiateViewControllerWithIdentifier:@"AllMembersController"];
    membersController.membersArr = [[LogicServices sharedInstance] getCurrentCommunity].members;
    [self presentViewController:membersController animated:YES completion:nil];
}

@end
