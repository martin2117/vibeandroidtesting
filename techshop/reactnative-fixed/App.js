import React from 'react';
import { Text } from 'react-native';
import { StatusBar } from 'expo-status-bar';
import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';
import { createBottomTabNavigator } from '@react-navigation/bottom-tabs';

import { AuthProvider, useAuth } from './src/context/AuthContext';
import { CartProvider } from './src/context/CartContext';
import LoginScreen from './src/screens/LoginScreen';
import CatalogScreen from './src/screens/CatalogScreen';
import CartScreen from './src/screens/CartScreen';
import CheckoutScreen from './src/screens/CheckoutScreen';
import ConfirmationScreen from './src/screens/ConfirmationScreen';

const Tab = createBottomTabNavigator();
const RootStack = createNativeStackNavigator();
const CartStack = createNativeStackNavigator();

function CartStackScreen() {
  return (
    <CartStack.Navigator>
      <CartStack.Screen name="Cart" component={CartScreen} options={{ title: 'Cart' }} />
      <CartStack.Screen name="Checkout" component={CheckoutScreen} options={{ title: 'Checkout' }} />
      <CartStack.Screen
        name="Confirmation"
        component={ConfirmationScreen}
        options={{ title: 'Confirmation', headerBackVisible: false }}
      />
    </CartStack.Navigator>
  );
}

function MainTabs() {
  return (
    <Tab.Navigator>
      {/* FIXED (BUG-014): the title is "Products".
          Tab items carry stable testIDs + labels so runners can locate the
          bottom-nav tabs reliably across iOS versions (parity with SwiftUI's
          tab identifiers). */}
      <Tab.Screen
        name="Products"
        component={CatalogScreen}
        options={{
          headerTitle: () => (
            <Text testID="catalog-title" style={{ fontSize: 18, fontWeight: '700', color: '#1a1442' }}>
              Products
            </Text>
          ),
          title: 'Products',
          tabBarAccessibilityLabel: 'Products',
          tabBarTestID: 'tab-products',
        }}
      />
      <Tab.Screen
        name="Cart"
        component={CartStackScreen}
        options={{ title: 'Cart', tabBarAccessibilityLabel: 'Cart', tabBarTestID: 'tab-cart', headerShown: false }}
      />
    </Tab.Navigator>
  );
}

function AppNavigator() {
  const { isAuthenticated } = useAuth();

  // FIXED (BUG-015): the tab bar exists only after authentication. Before
  // login, the login screen is shown alone — no tabs, nothing reachable.
  return (
    <RootStack.Navigator screenOptions={{ headerShown: false }}>
      {isAuthenticated ? (
        <RootStack.Screen name="Main" component={MainTabs} />
      ) : (
        <RootStack.Screen name="Login" component={LoginScreen} />
      )}
    </RootStack.Navigator>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <CartProvider>
        <NavigationContainer>
          <AppNavigator />
          <StatusBar style="light" />
        </NavigationContainer>
      </CartProvider>
    </AuthProvider>
  );
}
