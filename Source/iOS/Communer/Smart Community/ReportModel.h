//
//  ReportModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "LocationModel.h"
#import "KVCBaseObject.h"

@interface ReportModel : KVCBaseObject

@property (nonatomic, strong) LocationModel* location;
@property (nonatomic, strong) NSString* type;
@property (nonatomic, strong) NSString* desc;
@property (nonatomic) BOOL isPoliceReport;
@property (nonatomic) BOOL isAnnonimus;
@property (nonatomic, strong) NSString* severity;

@end
