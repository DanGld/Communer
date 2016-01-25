//
//  CommunitySearchModel.h
//  Smart Community
//
//  Created by oren shany on 10/15/15.
//  Copyright © 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LocationModel.h"
#import "KVCBaseObject.h"

@interface CommunitySearchModel : KVCBaseObject

@property (nonatomic, strong) NSString* communityID;
@property (nonatomic, strong) NSString* email;
@property (nonatomic, strong) NSString* imageURL;
@property (nonatomic, strong) LocationModel* location;
@property (nonatomic, strong) NSString* name;
@property (nonatomic, strong) NSString* phone;
@property (nonatomic, strong) NSString* type;
@property (nonatomic, strong) NSString* website;
@property (nonatomic, strong) NSString* roofOrg;



@end
