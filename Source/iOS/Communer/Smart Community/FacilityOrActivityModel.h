//
//  FacilityOrActivityModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LocationModel.h"
#import "CommunityContactModel.h"
#import "KVCBaseObject.h"

@interface FacilityOrActivityModel : KVCBaseObject

@property (nonatomic, strong) NSString* facilityID;
@property (nonatomic, strong) NSString* bigImageUrl;
@property (nonatomic, strong) NSString* name;
@property (nonatomic, strong) NSString* descr;
@property (nonatomic, strong) NSArray* images;
@property (nonatomic, strong) LocationModel* location;
@property (nonatomic, strong) NSMutableArray* events;
@property (nonatomic, strong) CommunityContactModel* communityContact;

@end
