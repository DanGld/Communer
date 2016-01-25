//
//  SecurityEventModel.h
//  Smart Community
//
//  Created by oren shany on 8/6/15.
//  Copyright (c) 2015 Moveo. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ReportModel.h"
#import "KVCBaseObject.h"

@interface SecurityEventModel : KVCBaseObject

@property (nonatomic, strong) ReportModel* report;
@property (nonatomic, strong) NSString* reporterName;

@end
