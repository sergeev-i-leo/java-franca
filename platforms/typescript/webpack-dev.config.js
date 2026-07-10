const HtmlWebpackPlugin = require("html-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const CopyWebpackPlugin = require('copy-webpack-plugin');
const path = require('path');
require('dotenv').config({ path: './.env' });

module.exports = {
  entry: `./src/index.ts`,
  devtool: 'source-map',
  module: {
    rules: [
      {
        test: /\.md$/,
        type: 'asset/source'
      },
      {
        test: /\.html$/,
        use: [
          "html-loader",
          {
            loader: "posthtml-loader",
            options: {
              plugins: [
                require("posthtml-include")({
                  root: path.resolve(__dirname, "src"),
                }),
              ],
            },
          },
        ],
      },
      {
        test: /favicon\.ico$/,
        type: 'asset/resource',
        generator: {
          filename: "[name][ext]"
        }
      },
      {
        test: /\.(js|jsx|ts|tsx)$/,
        exclude: /node_modules/,
        use: {
          loader: "ts-loader"
        }
      },
      {
        test: /\.css$/,
        use: [MiniCssExtractPlugin.loader, 'css-loader']
      },
      {
        test: /\.(s(a|c)ss)$/,
        use: [MiniCssExtractPlugin.loader, 'css-loader', 'sass-loader']
      },
      {
        test: /\.(png|jpg|jpeg|gif|svg)$/i,
        type: 'asset/resource',
        generator: {
          filename: 'assets/images/[name][ext]',
          publicPath: '/'
        }
      },
      {
        test: /\.(ttf|woff|woff2|eot|otf)$/,
        type: 'asset/resource',
        generator: {
          filename: 'assets/fonts/[name][ext]'
        }
      },
      {
        test: /\.zip$/,
        type: 'asset/resource',
        generator: {
          filename: 'assets/files/[name][ext]',
          publicPath: '/'
        }
      }
    ],
  },
  resolve: {
    extensions: ['.ts', '.tsx', '.js', '.json', '.png', '.jpg', '.svg', '.md'],
    alias: {
      '@java-franca': path.resolve(__dirname, './src/java-franca'),
      '@java-franca-native': path.resolve(__dirname, './src/java-franca-native'),
      '~assets': path.resolve(__dirname, './src/assets'),
      '~images': path.resolve(__dirname, './src/assets/images')
    }
  },
  output: {
    path: path.resolve(__dirname, 'build'),
    filename: 'bundle.js',
    publicPath: '/',
    clean: true
  },
  plugins: [
    new MiniCssExtractPlugin(),
    new HtmlWebpackPlugin({
      title: 'step-js',
      template: `src/index.html`,
      favicon: `src/favicon.ico`,
    })
  ],
  devServer: {
    port: 3000,
    host: '0.0.0.0',
    allowedHosts: 'all',
    historyApiFallback: true,
    headers: {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, PATCH, OPTIONS",
      "Access-Control-Allow-Headers": "X-Requested-With, content-type, Authorization"
    }
  },
};
