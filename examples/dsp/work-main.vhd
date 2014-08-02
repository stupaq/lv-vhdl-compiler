library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

library ieee_proposed;
use ieee_proposed.fixed_pkg.all;

library work;
use work.the_library.all;

entity main is
    port (clk_50m : in std_logic;
        btn_east : in std_logic;
        btn_north : in std_logic;
        btn_south : in std_logic;
        btn_west : in std_logic;
        rot_center : in std_logic;
        rot_a : in std_logic;
        rot_b : in std_logic;
        sw : in std_logic_vector(3 downto 0);
        led : out std_logic_vector(7 downto 0) := (others => '0');
        dac1_out : out std_logic_vector(7 downto 0);
        rs232_dce_txd : out std_logic;
        fx2_io : out std_logic_vector(40 downto 35));
end;

architecture behavioral of main is
    constant clk_div : integer := 5;
    constant filter1_len : integer := 8;
    constant freq_len : integer := 24;
    constant param_len : integer := 16;
    constant phase_len : integer := 8;
    constant wave_len : integer := 8;
    signal clk_my : std_logic;
    signal params : air2_params;
    signal reset_air : std_logic;
    signal reset_all : std_logic;
    signal filter1 : sfixed(filter1_len - 1 downto 0);
    signal freq : unsigned(freq_len - 1 downto 0);
    signal phase : unsigned(phase_len - 1 downto 0);
    signal wave : signed(wave_len - 1 downto 0);
begin
    a0 : reset_all <= btn_north;
    b0 :
    entity work.divider(behavioral)
        generic map(ratio => clk_div)
        port map(reset => reset_all, clk_in => clk_50m, clk_out => clk_my);
    b1 : fx2_io(36) <= clk_50m;
    b2 : fx2_io(37) <= clk_my;
    c1 : dac1_out <= conv2dac1(wave) when sw(0) = '0' else
        conv2dac1(filter1);
    e0 :
    entity work.parameters(behavioral)
        generic map(freq_len => freq_len, param_len => param_len)
        port map(rst => reset_all, clk_my => clk_my, rot_a => rot_a, rot_b =>
            rot_b, btn_minus => btn_west, btn_plus => btn_east, mode => sw(3
            downto 1), freq => freq, params => params, rs_tx_pin =>
            rs232_dce_txd, fx2_io => fx2_io(40 downto 39));
    fx2_io(35) <= reset_air;
    k2 :
    entity work.phase_acc(behavioral)
        generic map(width_in => freq_len, width_out => phase_len)
        port map(rst => reset_all, clk => clk_my, mult => freq, phase => phase)
        ;
    l0 :
    entity work.sin_cos(behavioral)
        generic map(width_in => phase_len, width_out => wave_len)
        port map(rst => reset_all, clk => clk_my, phase_in => phase, sine =>
            wave, cosine => open, phase_out => open);
    l1 : fx2_io(38) <= not wave(7);
    m40 : reset_air <= reset_all or btn_south;
    m41 :
    entity work.air2(behavioral)
        generic map(param_len => param_len, width_in => wave_len, width_out =>
            filter1_len)
        port map(reset => reset_air, clk => clk_my, params => params, x0 =>
            to_sfixed(wave, wave_len - 1, 0), y0 => filter1);
end;

